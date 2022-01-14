package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.OnDeliveryTransferAssetCommand;
import com.gdn.warehouse.assetsmanagement.command.model.OnDeliveryTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.entity.SystemParam;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.Identity;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.helper.TransferAssetHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.helper.model.TransferAssetHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class OnDeliveryTransferAssetCommandImpl implements OnDeliveryTransferAssetCommand {
   @Autowired
   private TransferAssetRepository transferAssetRepository;

   @Autowired
   private TransferAssetHistoryHelper transferAssetHistoryHelper;

   @Autowired
   private AssetRepository assetRepository;

   @Override
   public Mono<Boolean> execute(OnDeliveryTransferAssetCommandRequest request) {
      return transferAssetRepository.findByTransferAssetNumber(request.getTransferAssetNumber())
            .flatMap(transferAsset -> {
               transferAsset.setStatus(TransferAssetStatus.ON_DELIVERY);
               transferAsset.setDeliveryDate(new Date(request.getDeliveryDate()));
               transferAsset.setDeliveryFee(request.getDeliveryFee());
               return transferAssetRepository.save(transferAsset);
            })
            .doOnSuccess(this::updateAssetStatus)
            .doOnSuccess(transferAsset -> saveToHistory(transferAsset,request))
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

   private SendEmailHelperRequest toSendEmailHelperRequestWarehouseManager(Tuple2<Pair<Maintenance, Item>, SystemParam> tuple2){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_MAINTENANCE_WH_MANAGER")
            .mailSubject("Approval Request for Asset Maintenance")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            //email warehouse
            .toEmail(tuple2.getT1().getLeft().getWarehouseManagerEmail())
            .identifierKey(StringConstants.MAINTENANCE_NUMBER)
            .identifierValue(tuple2.getT1().getLeft().getMaintenanceNumber())
            .emailVariables(constructVariableForTemplate(tuple2, Identity.WAREHOUSE_MANAGER))
            .build();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestUser(Tuple2<Pair<Maintenance, Item>,SystemParam> tuple2){
      return SendEmailHelperRequest.builder()
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_MAINTENANCE_USER")
            .mailSubject("Approval Notification for Asset Maintenance")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail("jason.setiadi@gdn-commerce.com;")
//            .toEmail(StringConstants.USER_EMAIL)
            .identifierKey(StringConstants.MAINTENANCE_NUMBER)
            .identifierValue(tuple2.getT1().getLeft().getMaintenanceNumber())
            .emailVariables(constructVariableForTemplate(tuple2,Identity.USER))
            .build();
   }

   private Map<String, Object> constructVariableForTemplate(TransferAsset transferAsset, String itemName) {
      String assetNumbers = String.join(", ",transferAsset.getAssetNumbers());
      Map<String, Object> variables = new HashMap<>();
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
