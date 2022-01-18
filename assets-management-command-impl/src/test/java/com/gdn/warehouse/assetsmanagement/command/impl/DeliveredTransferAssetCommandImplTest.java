package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.DeliveredTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.TransferAssetHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.TransferAssetHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeliveredTransferAssetCommandImplTest {

   @InjectMocks
   private DeliveredTransferAssetCommandImpl command;

   @Mock
   private TransferAssetRepository transferAssetRepository;

   @Mock
   private AssetRepository assetRepository;

   @Mock
   private TransferAssetHistoryHelper transferAssetHistoryHelper;

   @Mock
   private GenerateSequenceHelper generateSequenceHelper;

   @Mock
   private ItemRepository itemRepository;

   @Mock
   private SendEmailHelper sendEmailHelper;

   private DeliveredTransferAssetCommandRequest commandRequest;
   private Asset asset;
   private TransferAsset transferAsset,transferAsset2;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      commandRequest = DeliveredTransferAssetCommandRequest.builder()
            .transferAssetNumber("CODE").arrivalDate(1L).username("username").build();
      asset = Asset.builder().build();
      transferAsset = TransferAsset.builder().transferAssetNumber("TA-NUMBER1").assetNumbers(Arrays.asList("CODE")).destination("DESTINATION")
            .itemCode("ITEM-CODE").transferAssetType(TransferAssetType.BORROW).origin("ORIGIN")
            .destinationWarehouseManagerEmail("EMAIL").originWarehouseManagerEmail("EMAIL").build();
      transferAsset2 = TransferAsset.builder().transferAssetNumber("TA-NUMBER2").assetNumbers(Arrays.asList("CODE")).destination("DESTINATION")
            .destinationWarehouseManagerEmail("EMAIL").originWarehouseManagerEmail("EMAIL")
            .itemCode("ITEM-CODE").transferAssetType(TransferAssetType.RETURN).build();
      item = Item.builder().itemCode("CODE").itemName("NAME").build();
   }

   @Test
   public void execute_success_borrow() {
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.just(transferAsset));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(transferAssetRepository.save(any(TransferAsset.class))).thenReturn(Mono.just(transferAsset));
      when(transferAssetHistoryHelper.createTransferAssetHistory(any(TransferAssetHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      when(generateSequenceHelper.generateDocumentNumber(DocumentType.TRANSFER_ASSET)).thenReturn(Mono.just("TA-NUMBER"));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(transferAssetRepository,times(2)).save(any(TransferAsset.class));
      verify(transferAssetHistoryHelper).createTransferAssetHistory(any(TransferAssetHistoryHelperRequest.class));
      verify(generateSequenceHelper).generateDocumentNumber(DocumentType.TRANSFER_ASSET);
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test
   public void execute_success_return() {
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.just(transferAsset2));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(transferAssetRepository.save(any(TransferAsset.class))).thenReturn(Mono.just(transferAsset2));
      when(transferAssetHistoryHelper.createTransferAssetHistory(any(TransferAssetHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(transferAssetRepository).save(any(TransferAsset.class));
      verify(transferAssetHistoryHelper).createTransferAssetHistory(any(TransferAssetHistoryHelperRequest.class));
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail() {
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.empty());
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
   }
}