package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.json.helper.JsonHelper;
import com.gdn.warehouse.assetsmanagement.command.CreateMaintenanceReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CreateMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.helper.AssetValidatorHelper;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SchedulerPlatformHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.CreateScheduleHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceReminderRepository;
import com.gdn.warehouse.assetsmanagement.streaming.model.AssetsManagementTopics;
import com.gdn.warehouse.assetsmanagement.streaming.model.MaintenanceReminderEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CreateMaintenanceReminderCommandImpl implements CreateMaintenanceReminderCommand {

   @Autowired
   private MaintenanceReminderRepository maintenanceReminderRepository;

   @Autowired
   private GenerateSequenceHelper generateSequenceHelper;

   @Autowired
   private ScheduleHelper scheduleHelper;

   @Autowired
   private JsonHelper jsonHelper;

   @Autowired
   private SchedulerPlatformHelper schedulerPlatformHelper;

   @Autowired
   private AssetValidatorHelper assetValidatorHelper;

   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private ItemRepository itemRepository;


   @Override
   public Mono<String> execute(CreateMaintenanceReminderCommandRequest request) {
      List<String> assetNumbers = request.getAssetNumbers().stream().map(String::trim).distinct().collect(Collectors.toList());
      return assetValidatorHelper.validateAssetForMaintenanceReminder(assetNumbers)
            .flatMap(assets -> itemRepository.findByItemCode(assets.get(0).getItemCode())
                  .flatMap(item -> createMaintenanceReminder(request,item.getItemCode(),assets.get(0),assets,assetNumbers)))
            .doOnSuccess(this::assignScheduleToSchedulerPlatform)
            .map(MaintenanceReminder::getMaintenanceReminderNumber);
   }

   private Mono<MaintenanceReminder> createMaintenanceReminder(CreateMaintenanceReminderCommandRequest request, String itemCode,
                                                               Asset asset, List<Asset> assetList, List<String> assetNumbers){
      Calendar now = Calendar.getInstance();
      now.setTime(new Date(request.getScheduledDate()));
      now.set(Calendar.HOUR_OF_DAY,8);
      now.set(Calendar.MINUTE,0);
      now.set(Calendar.SECOND,0);
      Date newDate = now.getTime();
      List<String> emailList = request.getEmailList().stream().distinct().collect(Collectors.toList());
      return generateSequenceHelper.generateDocumentNumber(DocumentType.MAINTENANCE_REMINDER)
            .flatMap(maintenanceReminderNumber -> maintenanceReminderRepository.save(
                  MaintenanceReminder.builder()
                        .maintenanceReminderNumber(maintenanceReminderNumber)
                        .assetNumbers(assetNumbers)
                        .assetLocation(asset.getLocation())
                        .assetPoNumber(asset.getPoNumber())
                        .assetPoIssuedDate(asset.getPoIssuedDate())
                        .itemCode(itemCode)
                        .emailList(emailList)
                        .interval(request.getInterval())
                        .scheduledDate(newDate)
                        .enabled(Boolean.TRUE)
                        .deleted(Boolean.FALSE)
                        .createdBy(request.getUsername())
                        .createdDate(new Date())
                        .lastModifiedBy(request.getUsername())
                        .lastModifiedDate(new Date())
                        .build()))
            .flatMap(maintenanceReminder -> {
               assetList.forEach(asset1 -> asset1.setHasReminder(Boolean.TRUE));
               return assetRepository.saveAll(assetList).collectList()
                     .map(assets -> maintenanceReminder);
            });
   }

   private void assignScheduleToSchedulerPlatform(MaintenanceReminder maintenanceReminder){
      scheduleHelper.saveSchedule(constructCreateScheduleHelperRequest(maintenanceReminder))
            .doOnSuccess(schedulerPlatformHelper::sendToSchedulerPlatform).subscribe();
   }

   private CreateScheduleHelperRequest constructCreateScheduleHelperRequest(MaintenanceReminder maintenanceReminder){
      return CreateScheduleHelperRequest.builder()
            .identifier(maintenanceReminder.getMaintenanceReminderNumber())
            .nextSchedule(maintenanceReminder.getScheduledDate())
            .topic(AssetsManagementTopics.SCHEDULED_MAINTENANCE_REMINDER)
            .payload(jsonHelper.toJson(MaintenanceReminderEvent.builder()
                  .maintenanceReminderNumber(maintenanceReminder.getMaintenanceReminderNumber()).build()))
            .interval(maintenanceReminder.getInterval())
            .username(maintenanceReminder.getCreatedBy())
            .build();
   }
}
