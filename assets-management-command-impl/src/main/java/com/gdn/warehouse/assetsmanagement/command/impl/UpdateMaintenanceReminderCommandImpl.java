package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.json.helper.JsonHelper;
import com.gdn.warehouse.assetsmanagement.command.UpdateMaintenanceReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.helper.AssetValidatorHelper;
import com.gdn.warehouse.assetsmanagement.helper.DateHelper;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SchedulerPlatformHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.CreateScheduleHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceReminderRepository;
import com.gdn.warehouse.assetsmanagement.streaming.model.AssetsManagementTopics;
import com.gdn.warehouse.assetsmanagement.streaming.model.MaintenanceReminderEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateMaintenanceReminderCommandImpl implements UpdateMaintenanceReminderCommand {

   @Autowired
   private MaintenanceReminderRepository maintenanceReminderRepository;

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
   private DateHelper dateHelper;

   @Override
   public Mono<Boolean> execute(UpdateMaintenanceReminderCommandRequest request) {
      return dateHelper.validateScheduledDate(request.getScheduledDate())
            .flatMap(calendar -> maintenanceReminderRepository.findByMaintenanceReminderNumber(request.getMaintenanceReminderNumber())
                  .flatMap(maintenanceReminder -> {
                     List<String> assetNumbersRequest = request.getAssetNumbers().stream().map(String::trim).distinct().collect(Collectors.toList());
                     List<String> emailListRequest = request.getEmailList().stream().map(String::trim).distinct().collect(Collectors.toList());
                     return assetValidatorHelper.validateAssetFromRequest(assetNumbersRequest)
                           .flatMap(assets -> {
                              List<String> newAssets = new ArrayList<>(assetNumbersRequest);
                              newAssets.removeAll(maintenanceReminder.getAssetNumbers());
                              List<String> oldAssets = new ArrayList<>(maintenanceReminder.getAssetNumbers());
                              oldAssets.removeAll(assetNumbersRequest);
                              if(CollectionUtils.isNotEmpty(newAssets)){
                                 return assetRepository.findByAssetNumberIn(newAssets)
                                       .flatMap(this::validateReminder).flatMap(this::turnOnReminder).collectList()
                                       .flatMap(assets1 -> assetRepository.saveAll(assets1).collectList())
                                       .flatMap(assets1 -> assetRepository.findByAssetNumberIn(oldAssets)
                                             .flatMap(this::turnOffReminder).collectList()
                                             .flatMap(assets2 -> assetRepository.saveAll(assets2).collectList())
                                             .map(assets2 -> assets1));
                              }
                              return mono(()->assets);
                           }).flatMap(assets -> {
                              calendar.set(Calendar.HOUR_OF_DAY,8);
                              calendar.set(Calendar.MINUTE,0);
                              calendar.set(Calendar.SECOND,0);
                              Date newDate = calendar.getTime();
                              if (BooleanUtils.isTrue(request.getEnabled())){
                                 return updateMaintenanceReminder(request, maintenanceReminder, assetNumbersRequest,emailListRequest,assets.get(0).getItemCode(),newDate)
                                       .doOnSuccess(maintenanceReminder1 -> saveNewSchedule(maintenanceReminder1,request,newDate));
                              }else {
                                 return updateMaintenanceReminder(request, maintenanceReminder, assetNumbersRequest,emailListRequest,assets.get(0).getItemCode(),newDate)
                                       .doOnSuccess(this::disableSchedule);
                              }}).flatMap(result->mono(()->Boolean.TRUE));
                  }));
   }

   private void saveNewSchedule(MaintenanceReminder maintenanceReminder,UpdateMaintenanceReminderCommandRequest request,
                                Date scheduledDate){
      scheduleHelper.saveSchedule(CreateScheduleHelperRequest.builder()
                  .identifier(maintenanceReminder.getMaintenanceReminderNumber())
                  .nextSchedule(scheduledDate)
                  .topic(AssetsManagementTopics.SCHEDULED_MAINTENANCE_REMINDER)
                  .payload(jsonHelper.toJson(MaintenanceReminderEvent.builder()
                        .maintenanceReminderNumber(maintenanceReminder.getMaintenanceReminderNumber()).build()))
                  .interval(request.getInterval())
                  .username(request.getUsername()).build())
            .doOnSuccess(schedulerPlatformHelper::sendToSchedulerPlatform).subscribe();
   }

   private void disableSchedule(MaintenanceReminder maintenanceReminder){
      scheduleHelper.cancelSchedule(maintenanceReminder.getMaintenanceReminderNumber(),
                  ObjectUtils.isEmpty(maintenanceReminder.getPreviousExecutionTime())?null:maintenanceReminder.getPreviousExecutionTime())
            .doOnSuccess(schedulerPlatformHelper::sendCancellationToSchedulerPlatform).subscribe();
   }

   private Mono<MaintenanceReminder> updateMaintenanceReminder(UpdateMaintenanceReminderCommandRequest request, MaintenanceReminder maintenanceReminder, List<String> assetNumbersRequest,
                                                            List<String> emailListRequest, String itemCode, Date scheduledDate) {
      maintenanceReminder.setEnabled(request.getEnabled());
      maintenanceReminder.setScheduledDate(scheduledDate);
      maintenanceReminder.setInterval(request.getInterval());
      maintenanceReminder.setAssetNumbers(assetNumbersRequest);
      maintenanceReminder.setEmailList(emailListRequest);
      maintenanceReminder.setLastModifiedBy(request.getUsername());
      maintenanceReminder.setLastModifiedDate(new Date());
      maintenanceReminder.setItemCode(itemCode);
      return maintenanceReminderRepository.save(maintenanceReminder);
   }

   private Mono<Asset> validateReminder(Asset asset){
      if(asset.getHasReminder()){
         return Mono.defer(()->Mono.error(new CommandErrorException("There is an active Maintenance Reminder for" +
               " asset number: " + asset.getAssetNumber() + " !", HttpStatus.BAD_REQUEST)));
      }else {
         return Mono.defer(()->Mono.just(asset));
      }
   }

   private Mono<Asset> turnOnReminder(Asset asset){
      asset.setHasReminder(Boolean.TRUE);
      return mono(()->asset);
   }

   private Mono<Asset> turnOffReminder(Asset asset){
      asset.setHasReminder(Boolean.FALSE);
      return mono(()->asset);
   }
}
