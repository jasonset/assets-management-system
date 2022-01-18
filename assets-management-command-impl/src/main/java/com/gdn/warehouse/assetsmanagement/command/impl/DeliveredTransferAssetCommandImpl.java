package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.DeliveredTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.DeliveredTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.TransferAssetHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.helper.model.TransferAssetHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class DeliveredTransferAssetCommandImpl implements DeliveredTransferAssetCommand {

   @Autowired
   private TransferAssetRepository transferAssetRepository;

   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private TransferAssetHistoryHelper transferAssetHistoryHelper;

   @Autowired
   private GenerateSequenceHelper generateSequenceHelper;

   @Autowired
   private SendEmailHelper sendEmailHelper;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<Boolean> execute(DeliveredTransferAssetCommandRequest request) {
      return transferAssetRepository.findByTransferAssetNumber(request.getTransferAssetNumber())
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Transfer Asset doesn't exist!", HttpStatus.BAD_REQUEST))))
            .flatMap(transferAsset -> updateTransferAssetStatus(transferAsset,request))
            .flatMap(transferAsset -> createReturnTransferAsset(transferAsset)
                  .flatMap(transferAsset1 -> itemRepository.findByItemCode(transferAsset.getItemCode())
                        .doOnSuccess(item -> {
                           updateAssets(transferAsset.getAssetNumbers(),transferAsset.getDestination(),transferAsset.getTransferAssetType());
                           saveToHistory(transferAsset,request);
                           sendEmailHelper.sendEmail(toSendEmailHelperRequestWarehouseManagerDestination(transferAsset,item));
                           sendEmailHelper.sendEmail(toSendEmailHelperRequestWarehouseManagerOrigin(transferAsset,item));
                           sendEmailHelper.sendEmail(toSendEmailHelperRequestUser(transferAsset,item));
                        })))
            .map(result -> Boolean.TRUE);
   }

   private Mono<TransferAsset> updateTransferAssetStatus(TransferAsset transferAsset, DeliveredTransferAssetCommandRequest request){
      transferAsset.setArrivalDate(new Date(request.getArrivalDate()));
      transferAsset.setStatus(TransferAssetStatus.DELIVERED);
      transferAsset.setLastModifiedBy(request.getUsername());
      transferAsset.setLastModifiedDate(new Date());
      return transferAssetRepository.save(transferAsset);
   }

   private void saveToHistory(TransferAsset transferAsset, DeliveredTransferAssetCommandRequest request){
      transferAssetHistoryHelper.createTransferAssetHistory(toTransferAssetHistoryHelperRequest(transferAsset,request)).subscribe();
   }

   private TransferAssetHistoryHelperRequest toTransferAssetHistoryHelperRequest(TransferAsset transferAsset, DeliveredTransferAssetCommandRequest request){
      return TransferAssetHistoryHelperRequest.builder()
            .transferAssetNumber(transferAsset.getTransferAssetNumber())
            .transferAssetType(transferAsset.getTransferAssetType())
            .transferAssetStatus(transferAsset.getStatus())
            .updatedBy(request.getUsername())
            .updatedDate(new Date())
            .build();
   }

   private Mono<TransferAsset> createReturnTransferAsset(TransferAsset transferAsset){
      if(TransferAssetType.BORROW.equals(transferAsset.getTransferAssetType())){
         return generateSequenceHelper.generateDocumentNumber(DocumentType.TRANSFER_ASSET)
               .switchIfEmpty(Mono.defer(()-> Mono.error(new CommandErrorException("Failed to Generate Document Number for Transfer Asset",HttpStatus.INTERNAL_SERVER_ERROR))))
               .flatMap(documentNumber -> transferAssetRepository.save(TransferAsset.builder()
                     .transferAssetNumber(documentNumber)
                     .assetNumbers(transferAsset.getAssetNumbers())
                     .itemCode(transferAsset.getItemCode())
                     .origin(transferAsset.getDestination())
                     .destination(transferAsset.getOrigin())
                     .status(TransferAssetStatus.APPROVED)
                     .notes(transferAsset.getNotes())
                     .arrivalDate(null)
                     .deliveryDate(null)
                     .transferAssetType(TransferAssetType.RETURN)
                     .originWarehouseManagerEmail(transferAsset.getDestinationWarehouseManagerEmail())
                     .destinationWarehouseManagerEmail(transferAsset.getOriginWarehouseManagerEmail())
                     .createdBy(StringConstants.SYSTEM)
                     .createdDate(new Date())
                     .lastModifiedBy(StringConstants.SYSTEM)
                     .lastModifiedDate(new Date())
                     .referenceNumber(transferAsset.getTransferAssetNumber()).build()));
      }else {
         return mono(()->transferAsset);
      }
   }

   private void updateAssets(List<String> assetNumbers, String location,TransferAssetType transferAssetType){
      assetRepository.findByAssetNumberIn(assetNumbers)
            .map(asset -> {
               asset.setStatus(AssetStatus.NORMAL);
               asset.setLocation(location);
               if(TransferAssetType.RETURN.equals(transferAssetType)){
                  asset.setInBorrow(Boolean.FALSE);
               }
               return asset;
            }).collectList()
            .flatMap(assets -> assetRepository.saveAll(assets).collectList()).subscribe();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestWarehouseManagerDestination(TransferAsset transferAsset, Item item){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_TRANSFER_ASSET_DELIVERED")
            .mailSubject("Delivered Transfer Asset")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            //email warehouse
            .toEmail(transferAsset.getDestinationWarehouseManagerEmail())
            .identifierKey(StringConstants.TRANSFER_ASSET_NUMBER)
            .identifierValue(transferAsset.getTransferAssetNumber())
            .emailVariables(constructVariableForTemplate(transferAsset,item.getItemName(), "WH Manager "+transferAsset.getDestination()))
            .build();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestWarehouseManagerOrigin(TransferAsset transferAsset, Item item){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_TRANSFER_ASSET_DELIVERED")
            .mailSubject("Delivered Transfer Asset")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            //email warehouse
            .toEmail(transferAsset.getOriginWarehouseManagerEmail())
            .identifierKey(StringConstants.TRANSFER_ASSET_NUMBER)
            .identifierValue(transferAsset.getTransferAssetNumber())
            .emailVariables(constructVariableForTemplate(transferAsset,item.getItemName(), "WH Manager "+transferAsset.getOrigin()))
            .build();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestUser(TransferAsset transferAsset, Item item){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_TRANSFER_ASSET_DELIVERED")
            .mailSubject("Delivered Transfer Asset")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail("jason.setiadi@gdn-commerce.com;")
//            .toEmail(StringConstants.USER_EMAIL)
            .identifierKey(StringConstants.TRANSFER_ASSET_NUMBER)
            .identifierValue(transferAsset.getTransferAssetNumber())
            .emailVariables(constructVariableForTemplate(transferAsset,item.getItemName(),"All"))
            .build();
   }

   private Map<String, Object> constructVariableForTemplate(TransferAsset transferAsset, String itemName, String receiver) {
      String assetNumbers = String.join(", ",transferAsset.getAssetNumbers());
      Map<String, Object> variables = new HashMap<>();
      variables.put("receiver",receiver);
      variables.put("transferAssetNumber",transferAsset.getTransferAssetNumber());
      variables.put("itemName",itemName);
      variables.put("assetNumbers",assetNumbers);
      variables.put("assetQuantity",transferAsset.getAssetNumbers().size());
      variables.put("origin",transferAsset.getOrigin());
      variables.put("destination",transferAsset.getDestination());
      variables.put("notes", StringUtils.isEmpty(transferAsset.getNotes())?"-":transferAsset.getNotes());
      return variables;
   }
}
