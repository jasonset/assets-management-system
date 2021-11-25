package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.CreateMaintenanceRequestCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.entity.SystemParam;
import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.helper.AssetValidatorHelper;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceRepository;
import com.gdn.warehouse.assetsmanagement.repository.SystemParamRepository;
import com.gdn.warehouse.assetsmanagement.repository.WarehouseRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class CreateMaintenanceRequestCommandImplTest {
   
   @InjectMocks
   private CreateMaintenanceRequestCommandImpl command;

   @Mock
   private MaintenanceRepository maintenanceRepository;

   @Mock
   private GenerateSequenceHelper generateSequenceHelper;

   @Mock
   private SendEmailHelper sendEmailHelper;

   @Mock
   private WarehouseRepository warehouseRepository;

   @Mock
   private AssetValidatorHelper assetValidatorHelper;

   @Mock
   private SystemParamRepository systemParamRepository;

   @Mock
   private AssetRepository assetRepository;

   @Mock
   private ItemRepository itemRepository;

   private Maintenance maintenance;
   private CreateMaintenanceRequestCommandRequest request;
   private Asset asset;
   private Warehouse warehouse;
   private SystemParam systemParam;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      
      request = CreateMaintenanceRequestCommandRequest.builder()
            .requester("REQUESTER")
            .assetNumbers(Arrays.asList("ASSET-NUMBER"))
            .tanggalKerusakan(new Date())
            .tanggalLaporan(new Date())
            .deskripsiKerusakan("RUSAK")
            .username("username").build();

      maintenance = Maintenance.builder().maintenanceNumber("MR-NUMBER").assetNumbers(Arrays.asList("asset-number")).warehouseManagerEmail("email").build();

      asset = Asset.builder().itemCode("ITEM-CODE").location("LOCATION")
            .poNumber("PO-NUMBER").poIssuedDate(new Date()).build();

      warehouse = Warehouse.builder().email("abc@gdn-commerce.com").build();
      systemParam = SystemParam.builder().value("link").build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute() {
      when(generateSequenceHelper.generateDocumentNumber(DocumentType.MAINTENANCE)).thenReturn(Mono.just("MT-NUMBER"));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      when(assetValidatorHelper.validateAssetFromRequest(anyList()))
            .thenReturn(Mono.just(Arrays.asList(asset)));
      when(systemParamRepository.findByKey(anyString())).thenReturn(Mono.just(systemParam));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(request).block();
      verify(generateSequenceHelper).generateDocumentNumber(DocumentType.MAINTENANCE);
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(warehouseRepository).findByWarehouseName(anyString());
      verify(sendEmailHelper,times(3)).sendEmail(any(SendEmailHelperRequest.class));
      verify(assetValidatorHelper).validateAssetFromRequest(anyList());
      verify(systemParamRepository).findByKey(anyString());
      verify(assetRepository).saveAll(anyList());
      verify(itemRepository).findByItemCode(anyString());
   }
}