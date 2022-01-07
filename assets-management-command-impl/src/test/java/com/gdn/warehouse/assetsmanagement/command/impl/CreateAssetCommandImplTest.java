package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.CreateAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.Organisation;
import com.gdn.warehouse.assetsmanagement.enums.Purchase;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.GenerateAssetNumberRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.WarehouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static java.lang.Thread.sleep;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
public class CreateAssetCommandImplTest {
   @InjectMocks
   private CreateAssetCommandImpl command;

   @Mock
   private AssetRepository assetRepository;

   @Mock
   private WarehouseRepository warehouseRepository;

   @Mock
   private GenerateSequenceHelper generateSequenceHelper;

   private CreateAssetCommandRequest commandRequest;
   private Asset asset;
   private Item item;
   private Warehouse warehouse;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = CreateAssetCommandRequest.builder().purchase(Purchase.BUY)
            .category(AssetCategory.MHE).organisation(Organisation.DJARUM).itemCode("ITEM-CODE").location("WAREHOUSE").username("username")
            .deliveryDate(1L).poIssuedDate(1L).build();
      asset = Asset.builder().assetNumber("ASSET-NUMBER").build();
      item = Item.builder().itemCode("CODE").itemName("NAME").build();
      warehouse = Warehouse.builder().warehouseCode("CODE").warehouseName("NAME").build();
   }

   @Test
   public void test_createAsset_assetNumber_notExist() {
      when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      when(generateSequenceHelper.generateDocumentNumberForAsset(any(DocumentType.class),
            any(GenerateAssetNumberRequest.class))).thenReturn(Mono.just("ASSET-NUMBER"));
      command.execute(commandRequest).block();
      verify(assetRepository).save(any(Asset.class));
      verify(generateSequenceHelper).generateDocumentNumberForAsset(any(DocumentType.class),
            any(GenerateAssetNumberRequest.class));
      verify(warehouseRepository).findByWarehouseName(anyString());
   }

   @Test
   public void test_createAsset_assetNumber_notExist_poIssued_deliveryDate() {
      commandRequest.setDeliveryDate(1L);
      commandRequest.setPoIssuedDate(1L);
      when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      when(generateSequenceHelper.generateDocumentNumberForAsset(any(DocumentType.class),
            any(GenerateAssetNumberRequest.class))).thenReturn(Mono.just("ASSET-NUMBER"));
      command.execute(commandRequest).block();
      verify(assetRepository).save(any(Asset.class));
      verify(generateSequenceHelper).generateDocumentNumberForAsset(any(DocumentType.class),
            any(GenerateAssetNumberRequest.class));
      verify(warehouseRepository).findByWarehouseName(anyString());
   }

   @Test
   public void test_createAsset_assetNumber_exist() {
      commandRequest.setAssetNumber("ASSET-NUMBER");
      when(assetRepository.save(any(Asset.class))).thenReturn(Mono.just(asset));
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.just(warehouse));
      command.execute(commandRequest).block();
      verify(assetRepository).save(any(Asset.class));
      verify(warehouseRepository).findByWarehouseName(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void test_createAsset_warehouse_exist() {
      commandRequest.setAssetNumber("ASSET-NUMBER");
      when(warehouseRepository.findByWarehouseName(anyString())).thenReturn(Mono.empty());
      command.execute(commandRequest).block();
      verify(warehouseRepository).findByWarehouseName(anyString());
   }

//   @Test
//   public void test_flux() throws InterruptedException {
//      System.out.println("Starts");
//
//      //flux emits one element per second
//      Flux<Character> flux = Flux.just('a', 'b', 'c', 'd')
//            .delayElements(Duration.ofSeconds(1));
//      //Observer 1 - takes 500ms to process
//      flux
//            .map(Character::toUpperCase)
//            .subscribe(i -> {
//               try {
//                  sleep(500);
//               } catch (InterruptedException e) {
//                  e.printStackTrace();
//               }
//               log.info("Observer-1 : " + i);
//            });
//      //Observer 2 - process immediately
//      flux.subscribe(i -> log.info("Observer-2 : " + i));
//
//      sleep(10000);
//      System.out.println("Ends");
//   }

}