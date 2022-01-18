package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.SendTransferAssetReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.helper.DateHelper;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class SendTransferAssetReminderCommandImplTest {

   @InjectMocks
   private SendTransferAssetReminderCommandImpl command;

   @Mock
   private TransferAssetRepository transferAssetRepository;

   @Mock
   private SendEmailHelper sendEmailHelper;

   @Mock
   private ItemRepository itemRepository;

   @Mock
   private ScheduleHelper scheduleHelper;

   @Mock
   private DateHelper dateHelper;

   private SendTransferAssetReminderCommandRequest commandRequest;
   private TransferAsset transferAsset;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      commandRequest =  SendTransferAssetReminderCommandRequest.builder().transferAssetNumber(StringConstants.TRANSFER_ASSET_NUMBER).build();
      transferAsset = TransferAsset.builder().transferAssetNumber(StringConstants.TRANSFER_ASSET_NUMBER)
            .itemCode("ITEM").destinationWarehouseManagerEmail("EMAIL").destination("DESTINATION")
            .assetNumbers(Arrays.asList("ASSET_NUMBER")).deliveryDate(new Date())
            .duration(new Date()).origin("ORIGIN").build();
      item = Item.builder().itemCode("CODE").itemName("NAME").build();
   }

   @Test
   public void execute() {
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.just(transferAsset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(dateHelper.convertDateForEmail(any(Date.class))).thenReturn("Friday, 01 August 2029");
      when(scheduleHelper.cancelSchedule(anyString(),any(Date.class))).thenReturn(Mono.just(new Schedule()));
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
      verify(itemRepository).findByItemCode(anyString());
      verify(dateHelper,times(2)).convertDateForEmail(any(Date.class));
      verify(scheduleHelper).cancelSchedule(anyString(),any(Date.class));
   }
}