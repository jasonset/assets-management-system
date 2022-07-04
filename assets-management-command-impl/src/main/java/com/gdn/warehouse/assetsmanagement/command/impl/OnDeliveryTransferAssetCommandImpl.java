package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.OnDeliveryTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.OnDeliveryTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.Identity;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.TransferAssetHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.helper.model.TransferAssetHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OnDeliveryTransferAssetCommandImpl implements OnDeliveryTransferAssetCommand {
   @Autowired
   private TransferAssetRepository transferAssetRepository;

   @Autowired
   private TransferAssetHistoryHelper transferAssetHistoryHelper;

   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private SendEmailHelper sendEmailHelper;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<Boolean> execute(OnDeliveryTransferAssetCommandRequest request) {
      return transferAssetRepository.findByTransferAssetNumber(request.getTransferAssetNumber())
            .flatMap(transferAsset -> {
               transferAsset.setStatus(TransferAssetStatus.ON_DELIVERY);
               transferAsset.setDeliveryDate(new Date(request.getDeliveryDate()));
               transferAsset.setDeliveryFee(request.getDeliveryFee());
               return transferAssetRepository.save(transferAsset)
                     .flatMap(transferAsset1 -> itemRepository.findByItemCode(transferAsset1.getItemCode()))
                     .doOnSuccess(item -> {
                        updateAssetStatus(transferAsset);
                        saveToHistory(transferAsset,request);
                        sendEmailHelper.sendEmail(toSendEmailHelperRequestWarehouseManager(transferAsset,item));
                        sendEmailHelper.sendEmail(toSendEmailHelperRequestUser(transferAsset,item));
                     });
            })
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Transfer Asset doesn't exist!", HttpStatus.BAD_REQUEST))))
            .map(result -> Boolean.TRUE);
   }

   private void updateAssetStatus(TransferAsset transferAsset){
      assetRepository.findByAssetNumberIn(transferAsset.getAssetNumbers())
            .map(asset -> {
               asset.setStatus(AssetStatus.ON_TRANSFER_DELIVERY);
               return asset;
            }).collectList()
            .flatMap(assets -> assetRepository.saveAll(assets).collectList()).subscribe();
   }

   private void saveToHistory(TransferAsset transferAsset, OnDeliveryTransferAssetCommandRequest request){
      transferAssetHistoryHelper.createTransferAssetHistory(toTransferAssetHistoryHelperRequest(transferAsset,request)).subscribe();
   }

   private TransferAssetHistoryHelperRequest toTransferAssetHistoryHelperRequest(TransferAsset transferAsset, OnDeliveryTransferAssetCommandRequest request){
      return TransferAssetHistoryHelperRequest.builder()
            .transferAssetNumber(transferAsset.getTransferAssetNumber())
            .transferAssetType(transferAsset.getTransferAssetType())
            .transferAssetStatus(transferAsset.getStatus())
            .updatedBy(request.getUsername())
            .updatedDate(new Date())
            .build();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestWarehouseManager(TransferAsset transferAsset, Item item){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_TRANSFER_ASSET_DESTINATION_DELIVERY")
            .mailSubject("On Delivery Transfer Asset")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            //email warehouse
            .toEmail(transferAsset.getDestinationWarehouseManagerEmail())
            .identifierKey(StringConstants.TRANSFER_ASSET_NUMBER)
            .identifierValue(transferAsset.getTransferAssetNumber())
            .emailVariables(constructVariableForTemplate(transferAsset,item.getItemName(),Identity.WAREHOUSE_MANAGER))
            .build();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestUser(TransferAsset transferAsset, Item item){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_TRANSFER_ASSET_DESTINATION_DELIVERY")
            .mailSubject("On Delivery Transfer Asset")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail("jason.setiadi@gdn-commerce.com;")
//            .toEmail(StringConstants.USER_EMAIL)
            .identifierKey(StringConstants.TRANSFER_ASSET_NUMBER)
            .identifierValue(transferAsset.getTransferAssetNumber())
            .emailVariables(constructVariableForTemplate(transferAsset,item.getItemName(),Identity.USER))
            .build();
   }

   private Map<String, Object> constructVariableForTemplate(TransferAsset transferAsset, String itemName, Identity identity) {
      String assetNumbers = String.join(", ",transferAsset.getAssetNumbers());
      Map<String, Object> variables = new HashMap<>();
      if(Identity.USER.equals(identity)){
         variables.put("receiver","All");
      }else {
         variables.put("receiver","WH Manager "+transferAsset.getDestination());
      }
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
