package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.SendTransferAssetReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.model.SendTransferAssetReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.ScheduleRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.WarehouseRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SendTransferAssetReminderCommandImpl implements SendTransferAssetReminderCommand {

   private TransferAssetRepository transferAssetRepository;
   private WarehouseRepository warehouseRepository;
   private SendEmailHelper sendEmailHelper;
   private ItemRepository itemRepository;
   private ScheduleHelper scheduleHelper;

   @Override
   public Mono<Boolean> execute(SendTransferAssetReminderCommandRequest request) {
      return transferAssetRepository.findByTransferAssetNumber(request.getTransferAssetNumber())
            .flatMap(transferAsset -> Mono.zip(warehouseRepository.findByWarehouseName(transferAsset.getDestination()),
                  itemRepository.findByItemCode(transferAsset.getItemCode()))
                  .flatMap(tuple2 -> sendEmailHelper.sendEmail()));
   }

   private void disableSchedule(TransferAsset transferAsset){
      scheduleHelper.cancelSchedule(transferAsset.getTransferAssetNumber(),new Date()).subscribe();
   }

   private SendEmailHelperRequest toSendEmailHelperRequestWHManager(TransferAsset transferAsset, Warehouse warehouse, Item item){
      return SendEmailHelperRequest.builder()
            .mailTemplateId()
            .mailSubject()
            .fromEmail()
            .toEmail()
            .identifierKey()
            .identifierValue()
            .emailVariables()
            .build()
   }

   private Map<String, Object> constructVariableForTemplateMaintenanceNotCreated(TransferAsset transferAsset, String itemName, String location) {
      String assetNumbers = String.join(", ",transferAsset.getAssetNumbers());
      Date newDate = new Date();
      LocalDate date = newDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      LocalDate nextDate = maintenanceReminder.getScheduledDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      String dateStr = date.format(DateTimeFormatter.ofPattern(StringConstants.EMAIL_DATE));
      String nextDateStr = nextDate.format(DateTimeFormatter.ofPattern(StringConstants.EMAIL_DATE));
      Map<String, Object> variables = new HashMap<>();
      variables.put("maintenanceNumber","-");
      variables.put("itemName",itemName);
      variables.put("assetNumbers",assetNumbers);
      variables.put("assetQuantity",maintenanceReminder.getAssetNumbers().size());
      variables.put("location",location);
      variables.put("scheduledDate",dateStr);
      variables.put("nextScheduledDate",nextDateStr);
      variables.put("notes","Maintenance tidak terbuat secara otomatis karena Asset tidak dalam status NORMAL");
      return variables;
   }
}
