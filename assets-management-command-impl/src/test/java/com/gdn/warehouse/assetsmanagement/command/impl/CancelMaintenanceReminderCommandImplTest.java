package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.CancelMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SchedulerPlatformHelper;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceReminderRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class CancelMaintenanceReminderCommandImplTest {

   @InjectMocks
   private CancelMaintenanceReminderCommandImpl command;

   @Mock
   private MaintenanceReminderRepository maintenanceReminderRepository;

   @Mock
   private ScheduleHelper scheduleHelper;

   @Mock
   private SchedulerPlatformHelper schedulerPlatformHelper;

   @Mock
   private AssetRepository assetRepository;

   private CancelMaintenanceReminderCommandRequest commandRequest;
   private MaintenanceReminder maintenanceReminder;
   private Schedule schedule;
   private Asset asset;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = CancelMaintenanceReminderCommandRequest.builder()
            .maintenanceReminderNumber("MR-NUMBER").username("username").build();

      maintenanceReminder = MaintenanceReminder.builder().maintenanceReminderNumber("MR-NUMBER")
            .assetNumbers(Arrays.asList("ASSET-NUMBER")).previousExecutionTime(new Date()).build();
      schedule = Schedule.builder().build();
      asset = Asset.builder().build();
   }

   @Test
   public void execute() {
      when(maintenanceReminderRepository.findByMaintenanceReminderNumber(anyString())).thenReturn(Mono.just(maintenanceReminder));
      when(maintenanceReminderRepository.save(any(MaintenanceReminder.class))).thenReturn(Mono.just(maintenanceReminder));
      when(scheduleHelper.cancelSchedule(anyString(),any(Date.class))).thenReturn(Mono.just(schedule));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      command.execute(commandRequest).block();
      verify(schedulerPlatformHelper).sendCancellationToSchedulerPlatform(any(Schedule.class));
      verify(maintenanceReminderRepository).findByMaintenanceReminderNumber(anyString());
      verify(maintenanceReminderRepository).save(any(MaintenanceReminder.class));
      verify(scheduleHelper).cancelSchedule(anyString(),any(Date.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
   }
}