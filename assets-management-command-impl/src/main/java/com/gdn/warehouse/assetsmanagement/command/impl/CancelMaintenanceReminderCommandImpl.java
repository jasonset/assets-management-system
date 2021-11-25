package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.CancelMaintenanceReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CancelMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SchedulerPlatformHelper;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceReminderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class CancelMaintenanceReminderCommandImpl implements CancelMaintenanceReminderCommand {

   @Autowired
   private MaintenanceReminderRepository maintenanceReminderRepository;

   @Autowired
   private AssetRepository assetRepository;

   @Autowired
   private ScheduleHelper scheduleHelper;

   @Autowired
   private SchedulerPlatformHelper schedulerPlatformHelper;

   @Override
   public Mono<Boolean> execute(CancelMaintenanceReminderCommandRequest request) {
      return maintenanceReminderRepository.findByMaintenanceReminderNumber(request.getMaintenanceReminderNumber())
            .flatMap(maintenanceReminder -> {
               maintenanceReminder.setEnabled(false);
               maintenanceReminder.setLastModifiedBy(request.getUsername());
               maintenanceReminder.setLastModifiedDate(new Date());
               return maintenanceReminderRepository.save(maintenanceReminder);
            }).flatMap(maintenanceReminder -> assetRepository.findByAssetNumberIn(maintenanceReminder.getAssetNumbers()).collectList()
                        .flatMap(assets -> {
                           assets.forEach(asset -> asset.setHasReminder(false));
                           return assetRepository.saveAll(assets).collectList();
                        }).flatMap(assets -> scheduleHelper.cancelSchedule(maintenanceReminder.getMaintenanceReminderNumber(),
                              maintenanceReminder.getPreviousExecutionTime())
                        .doOnSuccess(schedulerPlatformHelper::sendCancellationToSchedulerPlatform)))
            .map(result -> Boolean.TRUE);
   }
}
