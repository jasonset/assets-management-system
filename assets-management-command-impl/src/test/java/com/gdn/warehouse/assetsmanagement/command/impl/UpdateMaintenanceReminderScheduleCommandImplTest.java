package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceReminderScheduleCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.helper.DateHelper;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SchedulerPlatformHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.CreateScheduleHelperRequest;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceReminderRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceRepository;
import com.gdn.warehouse.assetsmanagement.repository.ScheduleRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class UpdateMaintenanceReminderScheduleCommandImplTest {

   @InjectMocks
   private UpdateMaintenanceReminderScheduleCommandImpl command;

   @Mock
   private MaintenanceReminderRepository maintenanceReminderRepository;

   @Mock
   private ScheduleRepository scheduleRepository;

   @Mock
   private ScheduleHelper scheduleHelper;

   @Mock
   private SchedulerPlatformHelper schedulerPlatformHelper;

   @Mock
   private GenerateSequenceHelper generateSequenceHelper;

   @Mock
   private MaintenanceRepository maintenanceRepository;

   @Mock
   private SendEmailHelper sendEmailHelper;

   @Mock
   private AssetRepository assetRepository;

   @Mock
   private ItemRepository itemRepository;

   @Mock
   private DateHelper dateHelper;

   private UpdateMaintenanceReminderScheduleCommandRequest commandRequest;
   private MaintenanceReminder maintenanceReminder;
   private Maintenance maintenance;
   private Schedule schedule;
   private Asset asset;
   private Item item;


   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = UpdateMaintenanceReminderScheduleCommandRequest.builder()
            .maintenanceReminderNumber("MR-NUMBER").build();
      maintenanceReminder = MaintenanceReminder.builder().maintenanceReminderNumber("MR-NUMBER")
            .assetLocation("LOCATION").assetNumbers(Arrays.asList("ASSET-NUMBER")).itemCode("CODE").assetPoNumber("PO-NUMBER")
            .assetPoIssuedDate(new Date()).scheduledDate(new Date()).interval(2)
            .emailList(Arrays.asList("abc@gdn-commerce.com","def@gdn-commerce.com")).build();
      maintenance = Maintenance.builder().assetNumbers(Arrays.asList("ASSET-NUMBER")).build();
      schedule = Schedule.builder().interval(2).payload("PAYLOAD").timeUnit(TimeUnit.DAYS).build();
      asset = Asset.builder().status(AssetStatus.NORMAL).build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute() {
      when(maintenanceReminderRepository.findByMaintenanceReminderNumber(anyString())).thenReturn(Mono.just(maintenanceReminder));
      when(scheduleRepository.findByIdentifier(anyString())).thenReturn(Mono.just(schedule));
      when(scheduleHelper.saveSchedule(any(CreateScheduleHelperRequest.class))).thenReturn(Mono.just(schedule));
      doNothing().when(schedulerPlatformHelper).sendToSchedulerPlatform(any(Schedule.class));
      when(maintenanceReminderRepository.save(any(MaintenanceReminder.class))).thenReturn(Mono.just(maintenanceReminder));
      when(generateSequenceHelper.generateDocumentNumber(DocumentType.MAINTENANCE)).thenReturn(Mono.just("MT-NUMBER"));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(dateHelper.convertDateForEmail(any(Date.class))).thenReturn("Friday, 01 August 2029");
      command.execute(commandRequest).block();
      verify(maintenanceReminderRepository).findByMaintenanceReminderNumber(anyString());
      verify(scheduleRepository).findByIdentifier(anyString());
      verify(scheduleHelper).saveSchedule(any(CreateScheduleHelperRequest.class));
      verify(schedulerPlatformHelper).sendToSchedulerPlatform(any(Schedule.class));
      verify(maintenanceReminderRepository).save(any(MaintenanceReminder.class));
      verify(sendEmailHelper).sendEmail(any(SendEmailHelperRequest.class));
      verify(generateSequenceHelper).generateDocumentNumber(DocumentType.MAINTENANCE);
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(itemRepository).findByItemCode(anyString());
      verify(dateHelper,times(2)).convertDateForEmail(any(Date.class));
   }

   @Test
   public void execute_not_NORMAL() {
      asset.setStatus(AssetStatus.PENDING_MAINTENANCE);
      when(maintenanceReminderRepository.findByMaintenanceReminderNumber(anyString())).thenReturn(Mono.just(maintenanceReminder));
      when(scheduleRepository.findByIdentifier(anyString())).thenReturn(Mono.just(schedule));
      when(scheduleHelper.saveSchedule(any(CreateScheduleHelperRequest.class))).thenReturn(Mono.just(schedule));
      doNothing().when(schedulerPlatformHelper).sendToSchedulerPlatform(any(Schedule.class));
      when(maintenanceReminderRepository.save(any(MaintenanceReminder.class))).thenReturn(Mono.just(maintenanceReminder));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(maintenanceReminderRepository).findByMaintenanceReminderNumber(anyString());
      verify(scheduleRepository).findByIdentifier(anyString());
      verify(scheduleHelper).saveSchedule(any(CreateScheduleHelperRequest.class));
      verify(schedulerPlatformHelper).sendToSchedulerPlatform(any(Schedule.class));
      verify(maintenanceReminderRepository).save(any(MaintenanceReminder.class));
      verify(sendEmailHelper).sendEmail(any(SendEmailHelperRequest.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(itemRepository).findByItemCode(anyString());
   }
}