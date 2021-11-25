package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.ApproveTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApproveTransferAssetCommandImplTest {
   @InjectMocks
   private ApproveTransferAssetCommandImpl command;

   @Mock
   private TransferAssetRepository transferAssetRepository;

   @Mock
   private AssetRepository assetRepository;

   @Mock
   private SendEmailHelper sendEmailHelper;

   @Mock
   private TransferAssetHistoryHelper transferAssetHistoryHelper;

   @Mock
   private ItemRepository itemRepository;

   private ApproveTransferAssetCommandRequest commandRequest;
   private TransferAsset transferAsset;
   private Asset asset;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = ApproveTransferAssetCommandRequest.builder().transferAssetNumber("CODE")
            .approve(Boolean.TRUE).username("username").build();
      transferAsset = TransferAsset.builder().assetNumbers(Arrays.asList("ASSET-NUMBER"))
            .transferAssetType(TransferAssetType.BORROW).itemCode("CODE").build();
      asset = Asset.builder().build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute_true() {
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.just(transferAsset));
      when(transferAssetRepository.save(any(TransferAsset.class))).thenReturn(Mono.just(transferAsset));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(transferAssetHistoryHelper.createTransferAssetHistory(any(TransferAssetHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
      verify(transferAssetRepository).save(any(TransferAsset.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(transferAssetHistoryHelper).createTransferAssetHistory(any(TransferAssetHistoryHelperRequest.class));
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_true_on_delivery() {
      transferAsset.setStatus(TransferAssetStatus.ON_DELIVERY);
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.just(transferAsset));
      when(transferAssetRepository.save(any(TransferAsset.class))).thenReturn(Mono.just(transferAsset));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
      verify(transferAssetRepository).save(any(TransferAsset.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
   }
   
   @Test
   public void execute_false(){
      commandRequest.setApprove(Boolean.FALSE);
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.just(transferAsset));
      when(transferAssetRepository.save(any(TransferAsset.class))).thenReturn(Mono.just(transferAsset));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(transferAssetHistoryHelper.createTransferAssetHistory(any(TransferAssetHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
      verify(transferAssetRepository).save(any(TransferAsset.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(transferAssetHistoryHelper).createTransferAssetHistory(any(TransferAssetHistoryHelperRequest.class));
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_false_fail(){
      commandRequest.setApprove(Boolean.FALSE);
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.empty());
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
   }
}