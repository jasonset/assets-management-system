package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.json.helper.JsonHelper;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.helper.AssetValidatorHelper;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SchedulerPlatformHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.CreateScheduleHelperRequest;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

public class UpdateMaintenanceReminderCommandImplTest {

   @InjectMocks
   private UpdateMaintenanceReminderCommandImpl command;

   @Mock
   private MaintenanceReminderRepository maintenanceReminderRepository;

   @Mock
   private ScheduleHelper scheduleHelper;

   @Mock
   private JsonHelper jsonHelper;

   @Mock
   private SchedulerPlatformHelper schedulerPlatformHelper;

   @Mock
   private AssetValidatorHelper assetValidatorHelper;

   @Mock
   private AssetRepository assetRepository;

   private UpdateMaintenanceReminderCommandRequest commandRequest;
   private MaintenanceReminder maintenanceReminder;
   private Asset asset,asset2,asset3;
   private Schedule schedule;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      commandRequest = UpdateMaintenanceReminderCommandRequest.builder()
            .maintenanceReminderNumber("MR-NUMBER")
            .enabled(Boolean.TRUE)
            .assetNumbers(Arrays.asList("ASSET-NUMBER3","ASSET-NUMBER1"))
            .emailList(Arrays.asList("abc@gdn-commerce.com","def@gdn-commerce.com"))
            .interval(1).scheduledDate(1L).username("username").build();
      maintenanceReminder = MaintenanceReminder.builder().assetNumbers(Arrays.asList("ASSET-NUMBER1","ASSET-NUMBER2"))
            .emailList(Arrays.asList("abc@gdn-commerce.com"))
            .maintenanceReminderNumber("MR-NUMBER")
            .scheduledDate(new Date())
            .enabled(Boolean.FALSE)
            .previousExecutionTime(null)
            .interval(2).build();
      asset = Asset.builder().assetNumber("ASSET-NUMBER1").location("LOCATION").poNumber("PO-NUMBER").poIssuedDate(new Date())
            .itemCode("ITEM-CODE").hasReminder(Boolean.FALSE).build();
      asset2 = Asset.builder().assetNumber("ASSET-NUMBER2").location("LOCATION").poNumber("PO-NUMBER").poIssuedDate(new Date())
            .itemCode("ITEM-CODE").hasReminder(Boolean.FALSE).build();
      asset3 = Asset.builder().assetNumber("ASSET-NUMBER3").location("LOCATION").poNumber("PO-NUMBER").poIssuedDate(new Date())
            .itemCode("ITEM-CODE").hasReminder(Boolean.FALSE).build();
      schedule = Schedule.builder().build();
   }

   @Test
   public void execute_success_enable() {
      when(maintenanceReminderRepository.findByMaintenanceReminderNumber(anyString())).thenReturn(Mono.just(maintenanceReminder));
      when(maintenanceReminderRepository.save(any(MaintenanceReminder.class))).thenReturn(Mono.just(maintenanceReminder));
      when(assetValidatorHelper.validateAssetFromRequest(anyList())).thenReturn(Mono.just(Arrays.asList(asset,asset3)));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset3)).thenReturn(Flux.just(asset2));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset3)).thenReturn(Flux.just(asset2));
      when(scheduleHelper.saveSchedule(any(CreateScheduleHelperRequest.class))).thenReturn(Mono.just(schedule));
      doNothing().when(schedulerPlatformHelper).sendToSchedulerPlatform(any(Schedule.class));
      command.execute(commandRequest).block();
      verify(maintenanceReminderRepository).findByMaintenanceReminderNumber(anyString());
      verify(maintenanceReminderRepository).save(any(MaintenanceReminder.class));
      verify(assetValidatorHelper).validateAssetFromRequest(anyList());
      verify(assetRepository,times(2)).findByAssetNumberIn(anyList());
      verify(assetRepository,times(2)).saveAll(anyList());
      verify(scheduleHelper).saveSchedule(any(CreateScheduleHelperRequest.class));
      verify(schedulerPlatformHelper).sendToSchedulerPlatform(any(Schedule.class));
   }

   @Test
   public void execute_success_disable() {
      commandRequest.setEnabled(Boolean.FALSE);
      maintenanceReminder.setEnabled(Boolean.TRUE);
      when(maintenanceReminderRepository.findByMaintenanceReminderNumber(anyString())).thenReturn(Mono.just(maintenanceReminder));
      when(maintenanceReminderRepository.save(any(MaintenanceReminder.class))).thenReturn(Mono.just(maintenanceReminder));
      when(assetValidatorHelper.validateAssetFromRequest(anyList())).thenReturn(Mono.just(Arrays.asList(asset,asset3)));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset3)).thenReturn(Flux.just(asset2));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset3)).thenReturn(Flux.just(asset2));
      when(scheduleHelper.cancelSchedule(anyString(),any())).thenReturn(Mono.just(schedule));
      doNothing().when(schedulerPlatformHelper).sendCancellationToSchedulerPlatform(any(Schedule.class));
      command.execute(commandRequest).block();
      verify(maintenanceReminderRepository).findByMaintenanceReminderNumber(anyString());
      verify(maintenanceReminderRepository).save(any(MaintenanceReminder.class));
      verify(assetValidatorHelper).validateAssetFromRequest(anyList());
      verify(assetRepository,times(2)).findByAssetNumberIn(anyList());
      verify(assetRepository,times(2)).saveAll(anyList());
      verify(scheduleHelper).cancelSchedule(anyString(),any());
      verify(schedulerPlatformHelper).sendCancellationToSchedulerPlatform(any(Schedule.class));
   }

   @Test
   public void execute_success_same_enabled() {
      maintenanceReminder.setEnabled(Boolean.TRUE);
      when(maintenanceReminderRepository.findByMaintenanceReminderNumber(anyString())).thenReturn(Mono.just(maintenanceReminder));
      when(maintenanceReminderRepository.save(any(MaintenanceReminder.class))).thenReturn(Mono.just(maintenanceReminder));
      when(assetValidatorHelper.validateAssetFromRequest(anyList())).thenReturn(Mono.just(Arrays.asList(asset,asset3)));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset3)).thenReturn(Flux.just(asset2));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset3)).thenReturn(Flux.just(asset2));
      command.execute(commandRequest).block();
      verify(maintenanceReminderRepository).findByMaintenanceReminderNumber(anyString());
      verify(maintenanceReminderRepository).save(any(MaintenanceReminder.class));
      verify(assetValidatorHelper).validateAssetFromRequest(anyList());
      verify(assetRepository,times(2)).findByAssetNumberIn(anyList());
      verify(assetRepository,times(2)).saveAll(anyList());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail_hasReminder_same_enabled() {
      maintenanceReminder.setEnabled(Boolean.TRUE);
      asset3.setHasReminder(Boolean.TRUE);
      when(maintenanceReminderRepository.findByMaintenanceReminderNumber(anyString())).thenReturn(Mono.just(maintenanceReminder));
      when(maintenanceReminderRepository.save(any(MaintenanceReminder.class))).thenReturn(Mono.just(maintenanceReminder));
      when(assetValidatorHelper.validateAssetFromRequest(anyList())).thenReturn(Mono.just(Arrays.asList(asset,asset3)));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset3)).thenReturn(Flux.just(asset2));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset3)).thenReturn(Flux.just(asset2));
      command.execute(commandRequest).block();
      verify(maintenanceReminderRepository).findByMaintenanceReminderNumber(anyString());
      verify(maintenanceReminderRepository).save(any(MaintenanceReminder.class));
      verify(assetValidatorHelper).validateAssetFromRequest(anyList());
      verify(assetRepository,times(2)).findByAssetNumberIn(anyList());
      verify(assetRepository,times(2)).saveAll(anyList());
   }

   @Test
   public void execute_success_same_enabled_no_new_asset() {
      commandRequest.setAssetNumbers(Arrays.asList("ASSET-NUMBER"));
      maintenanceReminder.setAssetNumbers(Arrays.asList("ASSET-NUMBER"));
      maintenanceReminder.setEnabled(Boolean.TRUE);
      when(maintenanceReminderRepository.findByMaintenanceReminderNumber(anyString())).thenReturn(Mono.just(maintenanceReminder));
      when(maintenanceReminderRepository.save(any(MaintenanceReminder.class))).thenReturn(Mono.just(maintenanceReminder));
      when(assetValidatorHelper.validateAssetFromRequest(anyList())).thenReturn(Mono.just(Arrays.asList(asset,asset3)));
      command.execute(commandRequest).block();
      verify(maintenanceReminderRepository).findByMaintenanceReminderNumber(anyString());
      verify(maintenanceReminderRepository).save(any(MaintenanceReminder.class));
      verify(assetValidatorHelper).validateAssetFromRequest(anyList());
   }
}