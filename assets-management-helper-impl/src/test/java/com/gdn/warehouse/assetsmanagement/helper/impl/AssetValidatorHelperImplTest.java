package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssetValidatorHelperImplTest {
   @InjectMocks
   private AssetValidatorHelperImpl helper;

   @Mock
   private AssetRepository assetRepository;

   private Asset asset;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      asset = Asset.builder().status(AssetStatus.NORMAL).assetNumber("ASSET-NUMBER").location("LOCATION")
            .hasReminder(Boolean.FALSE).itemCode("ITEM-CODE").build();
   }

   @Test
   public void validateAssetFromRequest() {
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      helper.validateAssetFromRequest(Arrays.asList("ASSET-NUMBER")).block();
      verify(assetRepository).findByAssetNumberIn(anyList());
   }

   @Test(expected = CommandErrorException.class)
   public void validateAssetFromRequest_fail_not_exist() {
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      helper.validateAssetFromRequest(Arrays.asList("ASSET-NUMBER","ASSET-NUMBER2")).block();
      verify(assetRepository).findByAssetNumberIn(anyList());
   }

   @Test(expected = CommandErrorException.class)
   public void validateAssetFromRequest_item_name_not_same() {
      Asset asset2 = Asset.builder().status(AssetStatus.NORMAL).assetNumber("ASSET-NUMBER2").location("LOCATION").itemCode("ITEM-CODE2").build();
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset,asset2));
      helper.validateAssetFromRequest(Arrays.asList("ASSET-NUMBER","ASSET-NUMBER2")).block();
      verify(assetRepository).findByAssetNumberIn(anyList());
   }

   @Test(expected = CommandErrorException.class)
   public void validateAssetFromRequest_status_not_NORMAL() {
      Asset asset2 = Asset.builder().status(AssetStatus.PENDING_TRANSFER).assetNumber("ASSET-NUMBER2").location("LOCATION").itemCode("ITEM-CODE2").build();
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset,asset2));
      helper.validateAssetFromRequest(Arrays.asList("ASSET-NUMBER","ASSET-NUMBER2")).block();
      verify(assetRepository).findByAssetNumberIn(anyList());
   }

   @Test
   public void validateAssetForMaintenanceReminder(){
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      helper.validateAssetForMaintenanceReminder(Arrays.asList("ASSET-NUMBER")).block();
      verify(assetRepository).findByAssetNumberIn(anyList());
   }

   @Test(expected = CommandErrorException.class)
   public void validateAssetForMaintenanceReminder_fail_has_reminder(){
      asset.setHasReminder(Boolean.TRUE);
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      helper.validateAssetForMaintenanceReminder(Arrays.asList("ASSET-NUMBER")).block();
      verify(assetRepository).findByAssetNumberIn(anyList());
   }
}