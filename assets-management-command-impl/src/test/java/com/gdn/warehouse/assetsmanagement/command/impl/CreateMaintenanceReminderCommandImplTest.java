package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.json.helper.JsonHelper;
import com.gdn.warehouse.assetsmanagement.command.model.CreateMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.helper.AssetValidatorHelper;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SchedulerPlatformHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.CreateScheduleHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

public class CreateMaintenanceReminderCommandImplTest {
   @InjectMocks
   private CreateMaintenanceReminderCommandImpl command;

   @Mock
   private MaintenanceReminderRepository maintenanceReminderRepository;

   @Mock
   private GenerateSequenceHelper generateSequenceHelper;

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

   @Mock
   private ItemRepository itemRepository;

   private CreateMaintenanceReminderCommandRequest commandRequest;
   private MaintenanceReminder maintenanceReminder;
   private Asset asset;
   private Schedule schedule;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      commandRequest = CreateMaintenanceReminderCommandRequest.builder()
            .assetNumbers(Arrays.asList("ASSET-NUMBER")).emailList(Arrays.asList("abc@gdn-commerce.com","def@gdn-commerce.com"))
            .interval(2).scheduledDate(Long.MAX_VALUE).username("username").build();
      maintenanceReminder = MaintenanceReminder.builder().maintenanceReminderNumber("MR-NUMBER").scheduledDate(new Date()).interval(2).build();
      asset = Asset.builder().assetNumber("ASSET-NUMBER").location("LOCATION").poNumber("PO-NUMBER").poIssuedDate(new Date())
            .itemCode("ITEM-CODE").build();
      schedule = Schedule.builder().build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute_success() {
      when(generateSequenceHelper.generateDocumentNumber(DocumentType.MAINTENANCE_REMINDER)).thenReturn(Mono.just("MR-NUMBER"));
      when(maintenanceReminderRepository.save(any(MaintenanceReminder.class))).thenReturn(Mono.just(maintenanceReminder));
      when(scheduleHelper.saveSchedule(any(CreateScheduleHelperRequest.class))).thenReturn(Mono.just(schedule));
      doNothing().when(schedulerPlatformHelper).sendToSchedulerPlatform(any(Schedule.class));
      when(assetValidatorHelper.validateAssetForMaintenanceReminder(anyList())).thenReturn(Mono.just(Arrays.asList(asset)));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(generateSequenceHelper).generateDocumentNumber(DocumentType.MAINTENANCE_REMINDER);
      verify(maintenanceReminderRepository).save(any(MaintenanceReminder.class));
      verify(scheduleHelper).saveSchedule(any(CreateScheduleHelperRequest.class));
      verify(schedulerPlatformHelper).sendToSchedulerPlatform(any(Schedule.class));
      verify(assetValidatorHelper).validateAssetForMaintenanceReminder(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail_scheduledDate_before_now(){
      commandRequest.setScheduledDate(1L);
      command.execute(commandRequest).block();
   }
}