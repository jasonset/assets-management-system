package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.OnDeliveryTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.helper.TransferAssetHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.TransferAssetHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
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

public class OnDeliveryTransferAssetCommandImplTest {
   @InjectMocks
   private OnDeliveryTransferAssetCommandImpl command;

   @Mock
   private TransferAssetRepository transferAssetRepository;

   @Mock
   private TransferAssetHistoryHelper transferAssetHistoryHelper;

   @Mock
   private AssetRepository assetRepository;

   private OnDeliveryTransferAssetCommandRequest commandRequest;
   private TransferAsset transferAsset;
   private Asset asset;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = OnDeliveryTransferAssetCommandRequest.builder().transferAssetNumber("TA-NUMBER")
            .deliveryDate(1L).username("name").build();
      transferAsset = TransferAsset.builder().transferAssetNumber("TA-NUMBER")
            .transferAssetType(TransferAssetType.MOVE).status(TransferAssetStatus.APPROVED)
            .assetNumbers(Arrays.asList("ASSET-NUMBER")).build();
      asset = Asset.builder().assetNumber("ASSET-NUMBER").build();
   }

   @Test
   public void execute() {
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.just(transferAsset));
      when(transferAssetRepository.save(any(TransferAsset.class))).thenReturn(Mono.just(transferAsset));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(transferAssetHistoryHelper.createTransferAssetHistory(any(TransferAssetHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
      verify(transferAssetRepository).save(any(TransferAsset.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(transferAssetHistoryHelper).createTransferAssetHistory(any(TransferAssetHistoryHelperRequest.class));
   }
}