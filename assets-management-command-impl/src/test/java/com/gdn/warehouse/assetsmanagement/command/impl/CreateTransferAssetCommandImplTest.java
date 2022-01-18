package com.gdn.warehouse.assetsmanagement.command.impl;


import com.blibli.oss.backend.json.helper.JsonHelper;
import com.gdn.warehouse.assetsmanagement.command.model.CreateTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.entity.SystemParam;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.helper.AssetValidatorHelper;
import com.gdn.warehouse.assetsmanagement.helper.DateHelper;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.SchedulerPlatformHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.CreateScheduleHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.SystemParamRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.WarehouseRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

public class CreateTransferAssetCommandImplTest {
   @InjectMocks
   private CreateTransferAssetCommandImpl command;

   @Mock
   private TransferAssetRepository transferAssetRepository;

   @Mock
   private GenerateSequenceHelper generateSequenceHelper;

   @Mock
   private WarehouseRepository warehouseRepository;

   @Mock
   private SendEmailHelper sendEmailHelper;

   @Mock
   private AssetValidatorHelper assetValidatorHelper;

   @Mock
   private AssetRepository assetRepository;

   @Mock
   private SystemParamRepository systemParamRepository;

   @Mock
   private ItemRepository itemRepository;

   @Mock
   private DateHelper dateHelper;

   @Mock
   private ScheduleHelper scheduleHelper;

   @Mock
   private SchedulerPlatformHelper schedulerPlatformHelper;

   @Mock
   private JsonHelper jsonHelper;

   private CreateTransferAssetCommandRequest commandRequest;
   private Asset asset;
   private TransferAsset transferAsset;
   private Warehouse warehouse,warehouse2;
   private SystemParam systemParam;
   private Item item;
   private Calendar calendar;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = CreateTransferAssetCommandRequest.builder().assetNumbers(Arrays.asList("ASSET-NUMBER"))
            .destination("DESTINATION")
            .notes("NOTES")
            .username("username")
            .transferAssetType(TransferAssetType.MOVE)
            .duration(1L)
            .build();
      asset = Asset.builder().status(AssetStatus.NORMAL).assetNumber("ASSET-NUMBER").location("LOCATION").itemCode("ITEM-CODE").build();
      transferAsset = TransferAsset.builder().transferAssetNumber("TA-CODE").assetNumbers(Arrays.asList("ASSET-NUMBER"))
            .transferAssetType(TransferAssetType.MOVE).build();
      warehouse = Warehouse.builder().warehouseCode("code1").email("abc@gdn-commerce.com").build();
      warehouse2 = Warehouse.builder().warehouseCode("code2").email("abc@gdn-commerce.com").build();
      systemParam = SystemParam.builder().value("link").build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
      calendar = Calendar.getInstance();
      calendar.setTime(new Date());
   }

   @Test
   public void execute_success_MOVE() {
      when(generateSequenceHelper.generateDocumentNumber(DocumentType.TRANSFER_ASSET)).thenReturn(Mono.just("TA-CODE"));
      when(transferAssetRepository.save(any(TransferAsset.class))).thenReturn(Mono.just(transferAsset));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse)).thenReturn(Mono.just(warehouse2));
      when(assetValidatorHelper.validateAssetFromRequest(anyList())).thenReturn(Mono.just(Arrays.asList(asset)));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(systemParamRepository.findByKey(anyString())).thenReturn(Mono.just(systemParam));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(generateSequenceHelper).generateDocumentNumber(DocumentType.TRANSFER_ASSET);
      verify(transferAssetRepository).save(any(TransferAsset.class));
      verify(warehouseRepository,times(2)).findByWarehouseName(anyString());
      verify(assetValidatorHelper).validateAssetFromRequest(anyList());
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(systemParamRepository).findByKey(anyString());
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test
   public void execute_success_BORROW() {
      commandRequest.setTransferAssetType(TransferAssetType.BORROW);
      transferAsset.setTransferAssetType(TransferAssetType.BORROW);
      when(dateHelper.validateScheduledDate(anyLong())).thenReturn(Mono.just(calendar));
      when(generateSequenceHelper.generateDocumentNumber(DocumentType.TRANSFER_ASSET)).thenReturn(Mono.just("TA-CODE"));
      when(transferAssetRepository.save(any(TransferAsset.class))).thenReturn(Mono.just(transferAsset));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse)).thenReturn(Mono.just(warehouse2));
      when(assetValidatorHelper.validateAssetFromRequest(anyList())).thenReturn(Mono.just(Arrays.asList(asset)));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(systemParamRepository.findByKey(anyString())).thenReturn(Mono.just(systemParam));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(scheduleHelper.saveSchedule(any(CreateScheduleHelperRequest.class))).thenReturn(Mono.just(new Schedule()));
      command.execute(commandRequest).block();
      verify(generateSequenceHelper).generateDocumentNumber(DocumentType.TRANSFER_ASSET);
      verify(transferAssetRepository).save(any(TransferAsset.class));
      verify(warehouseRepository,times(2)).findByWarehouseName(anyString());
      verify(assetValidatorHelper).validateAssetFromRequest(anyList());
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(systemParamRepository).findByKey(anyString());
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail_warehouse_same() {
      commandRequest.setDestination("LOCATION");
      when(generateSequenceHelper.generateDocumentNumber(DocumentType.TRANSFER_ASSET)).thenReturn(Mono.just("TA-CODE"));
      when(transferAssetRepository.save(any(TransferAsset.class))).thenReturn(Mono.just(transferAsset));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      when(assetValidatorHelper.validateAssetFromRequest(anyList())).thenReturn(Mono.just(Arrays.asList(asset)));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(systemParamRepository.findByKey(anyString())).thenReturn(Mono.just(systemParam));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(generateSequenceHelper).generateDocumentNumber(DocumentType.TRANSFER_ASSET);
      verify(transferAssetRepository).save(any(TransferAsset.class));
      verify(warehouseRepository,times(2)).findByWarehouseName(anyString());
      verify(assetValidatorHelper).validateAssetFromRequest(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(systemParamRepository).findByKey(anyString());
      verify(itemRepository).findByItemCode(anyString());
   }
}