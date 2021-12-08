package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.UpdateAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.Organisation;
import com.gdn.warehouse.assetsmanagement.enums.Purchase;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.WarehouseRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateAssetCommandImplTest {
   @InjectMocks
   private UpdateAssetCommandImpl command;

   @Mock
   private AssetRepository assetRepository;

   @Mock
   private ItemRepository itemRepository;

   @Mock
   private WarehouseRepository warehouseRepository;

   private UpdateAssetCommandRequest commandRequest;
   private Asset asset;
   private Item item;
   private Warehouse warehouse;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = UpdateAssetCommandRequest.builder()
            .assetNumber("ASSET")
            .itemCode("ITEM-CODE")
            .location("WAREHOUSE")
            .organisation(Organisation.DJARUM)
            .vendor("VENDOR")
            .poNumber("PO-NUMBER")
            .poIssuedDate(1231234L)
            .price(1000)
            .status(AssetStatus.NORMAL)
            .deliveryDate(1231234L)
            .notes("NOTES")
            .vehiclePlate(null)
            .nomorRangka(null)
            .nomorMesin(null)
            .purchase(Purchase.BUY)
            .username("USERNAME").build();
      asset = Asset.builder().assetNumber("ASSET").category(AssetCategory.MHE).status(AssetStatus.NORMAL).build();
      item = Item.builder().itemCode("CODE").itemName("ITEM-NAME").build();
      warehouse = Warehouse.builder().warehouseCode("CODE").warehouseName("WAREHOUSE").build();
   }

   @Test
   public void execute_success() {
      when(assetRepository.findByAssetNumber(anyString())).thenReturn(Mono.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));
      command.execute(commandRequest).block();
      verify(assetRepository).save(any(Asset.class));
      verify(itemRepository).findByItemCode(anyString());
      verify(warehouseRepository).findByWarehouseName(anyString());
      verify(assetRepository).findByAssetNumber(anyString());
   }

   @Test
   public void execute_success_value_null() {
      commandRequest.setPoIssuedDate(null);
      commandRequest.setPrice(null);
      commandRequest.setDeliveryDate(null);
      commandRequest.setVehiclePlate(null);
      commandRequest.setNomorMesin(null);
      commandRequest.setNomorRangka(null);
      when(assetRepository.findByAssetNumber(anyString())).thenReturn(Mono.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));
      command.execute(commandRequest).block();
      verify(assetRepository).save(any(Asset.class));
      verify(itemRepository).findByItemCode(anyString());
      verify(warehouseRepository).findByWarehouseName(anyString());
      verify(assetRepository).findByAssetNumber(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail_ON_MAINTENANCE() {
      asset.setStatus(AssetStatus.ON_MAINTENANCE);
      when(assetRepository.findByAssetNumber(anyString())).thenReturn(Mono.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));
      command.execute(commandRequest).block();
      verify(assetRepository).save(any(Asset.class));
      verify(itemRepository).findByItemCode(anyString());
      verify(warehouseRepository).findByWarehouseName(anyString());
      verify(assetRepository).findByAssetNumber(anyString());
   }

   @Test
   public void execute_success_RUSAK_PARAH_SUDAH_BAC() {
      asset.setStatus(AssetStatus.RUSAK_PARAH_SUDAH_BAC);
      when(assetRepository.findByAssetNumber(anyString())).thenReturn(Mono.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));
      command.execute(commandRequest).block();
      verify(assetRepository).save(any(Asset.class));
      verify(itemRepository).findByItemCode(anyString());
      verify(warehouseRepository).findByWarehouseName(anyString());
      verify(assetRepository).findByAssetNumber(anyString());
   }

   @Test
   public void execute_success_RUSAK_PARAH_BELUM_BAC() {
      asset.setStatus(AssetStatus.RUSAK_PARAH_BELUM_BAC);
      when(assetRepository.findByAssetNumber(anyString())).thenReturn(Mono.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));
      command.execute(commandRequest).block();
      verify(assetRepository).save(any(Asset.class));
      verify(itemRepository).findByItemCode(anyString());
      verify(warehouseRepository).findByWarehouseName(anyString());
      verify(assetRepository).findByAssetNumber(anyString());
   }

   @Test
   public void execute_success_ON_MAINTENANCE() {
      commandRequest.setStatus(AssetStatus.ON_MAINTENANCE);
      asset.setStatus(AssetStatus.ON_MAINTENANCE);
      when(assetRepository.findByAssetNumber(anyString())).thenReturn(Mono.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));
      command.execute(commandRequest).block();
      verify(assetRepository).save(any(Asset.class));
      verify(itemRepository).findByItemCode(anyString());
      verify(warehouseRepository).findByWarehouseName(anyString());
      verify(assetRepository).findByAssetNumber(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail() {
      when(assetRepository.findByAssetNumber(anyString())).thenReturn(Mono.empty());
      command.execute(commandRequest).block();
      verify(assetRepository).findByAssetNumber(anyString());
   }
}