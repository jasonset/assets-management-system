package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.ApproveTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.ApproveTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.Identity;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class ApproveTransferAssetCommandImpl implements ApproveTransferAssetCommand {

   @Autowired
   private TransferAssetRepository transferAssetRepository;

   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private SendEmailHelper sendEmailHelper;

   @Autowired
   private TransferAssetHistoryHelper transferAssetHistoryHelper;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<Boolean> execute(ApproveTransferAssetCommandRequest request) {
      return getTransferAsset(request.getTransferAssetNumber())
            .flatMap(this::validateStatus)
            .flatMap(transferAsset -> itemRepository.findByItemCode(transferAsset.getItemCode())
                  .flatMap(item -> updateTransferAssetStatus(transferAsset,request,item)))
            .flatMap(transferAsset -> saveToHistory(transferAsset,request))
            .doOnSuccess(this::updateAssetStatus)
            .map(result -> Boolean.TRUE);
   }

   private Mono<TransferAsset> getTransferAsset(String transferAssetNumber){
      return transferAssetRepository.findByTransferAssetNumber(transferAssetNumber)
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Transfer Asset doesn't exist!", HttpStatus.BAD_REQUEST))));
   }

   private Mono<TransferAsset> validateStatus(TransferAsset transferAsset){
      List<TransferAssetStatus> invalidStatuses = Arrays.asList(TransferAssetStatus.ON_DELIVERY,TransferAssetStatus.DECLINED,
            TransferAssetStatus.DELIVERED);
      if(invalidStatuses.contains(transferAsset.getStatus())){
         return Mono.defer(()->Mono.error(new CommandErrorException("Transfer Asset "+transferAsset.getTransferAssetNumber()+
               " cannot be APPROVED/DECLINED because status already in "+transferAsset.getStatus().name(),HttpStatus.BAD_REQUEST)));
      }else {
         return Mono.defer(()->Mono.just(transferAsset));
      }
   }

   private Mono<TransferAsset> updateTransferAssetStatus(TransferAsset transferAsset, ApproveTransferAssetCommandRequest request, Item item){
      if(BooleanUtils.isTrue(request.getApprove())){
         transferAsset.setStatus(TransferAssetStatus.APPROVED);
         sendEmailHelper.sendEmail(toSendEmailHelperRequestWarehouseManagerDestination(transferAsset,item.getItemName()));
         sendEmailHelper.sendEmail(toSendEmailHelperRequestUser(transferAsset,item.getItemName()));
      }else{
         transferAsset.setStatus(TransferAssetStatus.DECLINED);
      }
      transferAsset.setLastModifiedBy(request.getUsername());
      transferAsset.setLastModifiedDate(new Date());
      return transferAssetRepository.save(transferAsset);
   }

   private Mono<TransferAsset> saveToHistory(TransferAsset transferAsset, ApproveTransferAssetCommandRequest request){
      return transferAssetHistoryHelper.createTransferAssetHistory(toTransferAssetHistoryHelperRequest(transferAsset,request))
            .map(result-> transferAsset);
   }

   private TransferAssetHistoryHelperRequest toTransferAssetHistoryHelperRequest(TransferAsset transferAsset, ApproveTransferAssetCommandRequest request){
      return TransferAssetHistoryHelperRequest.builder()
            .transferAssetNumber(transferAsset.getTransferAssetNumber())
            .transferAssetType(transferAsset.getTransferAssetType())
            .transferAssetStatus(transferAsset.getStatus())
            .updatedBy(request.getUsername())
            .updatedDate(new Date())
            .build();
   }

   private void updateAssetStatus(TransferAsset transferAsset){
      if(TransferAssetStatus.APPROVED.equals(transferAsset.getStatus())){
         if(TransferAssetType.BORROW.equals(transferAsset.getTransferAssetType())){
            updateAssets(transferAsset.getAssetNumbers(),AssetStatus.ON_TRANSFER,Boolean.TRUE);
         }else {
            updateAssets(transferAsset.getAssetNumbers(),AssetStatus.ON_TRANSFER,Boolean.FALSE);
         }
      } else {
         updateAssets(transferAsset.getAssetNumbers(),AssetStatus.NORMAL,Boolean.FALSE);
      }
   }

   private void updateAssets(List<String> assetNumbers, AssetStatus newStatus, Boolean inBorrow){
      assetRepository.findByAssetNumberIn(assetNumbers)
            .map(asset -> {
               asset.setStatus(newStatus);
               asset.setInBorrow(inBorrow);
               return asset;
            }).collectList()
            .flatMap(assets -> assetRepository.saveAll(assets).collectList()).subscribe();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestWarehouseManagerDestination(TransferAsset transferAsset, String itemName){
      return SendEmailHelperRequest.builder()
            //TODO will arrive at warehouse/hub ${destination}
            //TODO ${receiver} user
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_TRANSFER_ASSET_APPROVED")
            .mailSubject("Transfer Asset Notification")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail(transferAsset.getDestinationWarehouseManagerEmail())
            .identifierKey(StringConstants.TRANSFER_ASSET_NUMBER)
            .identifierValue(transferAsset.getTransferAssetNumber())
            .emailVariables(constructVariableForTemplate(transferAsset,itemName,Identity.WAREHOUSE_MANAGER))
            .build();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestUser(TransferAsset transferAsset, String itemName){
      return SendEmailHelperRequest.builder()
            //TODO will arrive at warehouse/hub ${destination}
            //TODO ${receiver} user
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_TRANSFER_ASSET_APPROVED")
            .mailSubject("Transfer Asset Notification")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail(StringConstants.USER_EMAIL)
            .identifierKey(StringConstants.TRANSFER_ASSET_NUMBER)
            .identifierValue(transferAsset.getTransferAssetNumber())
            .emailVariables(constructVariableForTemplate(transferAsset,itemName,Identity.USER))
            .build();
   }

   private Map<String, Object> constructVariableForTemplate(TransferAsset transferAsset, String itemName, Identity identity) {
      String assetNumbers = String.join(", ",transferAsset.getAssetNumbers());
      Map<String, Object> variables = new HashMap<>();
      if(Identity.USER.equals(identity)){
         variables.put("receiver","All");
      } else {
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
