package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.json.helper.JsonHelper;
import com.gdn.warehouse.assetsmanagement.command.model.CreateMaintenanceReminderCommandRequest;
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
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
            .interval(2).scheduledDate(1L).username("username").build();
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

//   public int firstUniqChar(String str) {
//      int[] charCount = new int[26];
//      for (char c : str.toCharArray()) {
//         charCount[c - 'a']++;
//      }
//      for(int i = 0; i < str.length(); i++)
//      {
//         if(charCount[str.charAt(i)-'a'] == 1)
//            return i;
//      }
//      return -1;
//   }
//
//   public void reverse(){
//      List<String> colors = new ArrayList<>(Arrays.asList("RED", "BLUE", "BLACK"));
//
//      for (int i = 0, j = colors.size() - 1; i < j; i++) {
//         colors.add(i, colors.remove(j));
//      }
//
//      System.out.println(colors);
//   }
//
//   public List<Integer> shiftByOne(List<Integer> soal,int shift){
//      for (int i = 0;i<soal.size()-1;i++){
//         soal.add(0,soal.remove(soal.size()-1));
//      }
//      System.out.println(soal);
//      return soal;
//   }
//
//   @Test
//   public void test(){
////      Assertions.assertEquals(0,firstUniqChar("qz"));
////      reverse();
//      List<Integer> list = new ArrayList<Integer>();
//      list.add(1);
//      list.add(2);
//      list.add(3);
//      shiftByOne(list,1);
//   }

}