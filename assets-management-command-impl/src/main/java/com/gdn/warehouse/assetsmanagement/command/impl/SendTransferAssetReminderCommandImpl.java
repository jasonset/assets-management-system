package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.SendTransferAssetReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.model.SendTransferAssetReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.helper.DateHelper;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SendTransferAssetReminderCommandImpl implements SendTransferAssetReminderCommand {

   private TransferAssetRepository transferAssetRepository;
   private SendEmailHelper sendEmailHelper;
   private ItemRepository itemRepository;
   private ScheduleHelper scheduleHelper;
   private DateHelper dateHelper;

   @Override
   public Mono<Boolean> execute(SendTransferAssetReminderCommandRequest request) {
      return transferAssetRepository.findByTransferAssetNumber(request.getTransferAssetNumber())
            .flatMap(transferAsset -> itemRepository.findByItemCode(transferAsset.getItemCode())
                  .doOnSuccess(item -> {
                     disableSchedule(transferAsset);
                     sendEmailHelper.sendEmail(toSendEmailHelperRequestWHManager(transferAsset,item));
                  }))
            .flatMap(result -> mono(()->Boolean.TRUE));
   }

   private void disableSchedule(TransferAsset transferAsset){
      scheduleHelper.cancelSchedule(transferAsset.getTransferAssetNumber(),new Date()).subscribe();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestWHManager(TransferAsset transferAsset, Item item){
      return SendEmailHelperRequest.builder()
            //TODO mailTemplateId
            .mailTemplateId("EMAIL_ASSETS_MANAGEMENT_TRANSFER_ASSET_DURATION_REMINDER")
            .mailSubject("Transfer Asset Reminder")
            .fromEmail(StringConstants.SENDER_EMAIL_ASSETS_MANAGEMENT)
            .toEmail(transferAsset.getDestinationWarehouseManagerEmail())
            .identifierKey(StringConstants.TRANSFER_ASSET_NUMBER)
            .identifierValue(transferAsset.getTransferAssetNumber())
            .emailVariables(constructVariableForTransferAssetReminder(transferAsset,item.getItemName()))
            .build();
   }

   private Map<String, Object> constructVariableForTransferAssetReminder(TransferAsset transferAsset, String itemName) {
      String assetNumbers = String.join(", ",transferAsset.getAssetNumbers());
      String fromDateStr = dateHelper.convertDateForEmail(transferAsset.getDeliveryDate());
      String untilDateStr = dateHelper.convertDateForEmail(transferAsset.getDuration());
      Map<String, Object> variables = new HashMap<>();
      variables.put("receiver",transferAsset.getDestination());
      variables.put("itemName",itemName);
      variables.put("assetNumbers",assetNumbers);
      variables.put("assetQuantity",transferAsset.getAssetNumbers().size());
      variables.put("origin",transferAsset.getOrigin());
      variables.put("durationFrom",fromDateStr);
      variables.put("durationUntil",untilDateStr);
      return variables;
   }
}
