package com.gdn.warehouse.assetsmanagement.command.impl;


import com.gdn.warehouse.assetsmanagement.command.model.CreateTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.SystemParam;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.helper.AssetValidatorHelper;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
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

import static org.mockito.ArgumentMatchers.*;
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

   private CreateTransferAssetCommandRequest commandRequest;
   private Asset asset;
   private TransferAsset transferAsset;
   private Warehouse warehouse,warehouse2;
   private SystemParam systemParam;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = CreateTransferAssetCommandRequest.builder().assetNumbers(Arrays.asList("ASSET-NUMBER"))
            .destination("DESTINATION")
            .notes("NOTES")
            .username("username")
            .transferAssetType(TransferAssetType.MOVE)
            .build();
      asset = Asset.builder().status(AssetStatus.NORMAL).assetNumber("ASSET-NUMBER").location("LOCATION").itemCode("ITEM-CODE").build();
      transferAsset = TransferAsset.builder().transferAssetNumber("TA-CODE").assetNumbers(Arrays.asList("ASSET-NUMBER")).build();
      warehouse = Warehouse.builder().warehouseCode("code1").email("abc@gdn-commerce.com").build();
      warehouse2 = Warehouse.builder().warehouseCode("code2").email("abc@gdn-commerce.com").build();
      systemParam = SystemParam.builder().value("link").build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute_success() {
      when(generateSequenceHelper.generateDocumentNumber(DocumentType.TRANSFER_ASSET)).thenReturn(Mono.just("TA-CODE"));
      when(transferAssetRepository.save(any(TransferAsset.class))).thenReturn(Mono.just(transferAsset));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse)).thenReturn(Mono.just(warehouse2));
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