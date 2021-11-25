package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.GetAssetDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.Organisation;
import com.gdn.warehouse.assetsmanagement.enums.Purchase;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAssetDetailCommandImplTest {

   @InjectMocks
   private GetAssetDetailCommandImpl command;

   @Mock
   private AssetRepository assetRepository;

   @Mock
   private ItemRepository itemRepository;

   private GetAssetDetailCommandRequest commandRequest;
   private Asset asset;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = GetAssetDetailCommandRequest.builder().assetNumber("ASSET-NUMBER").build();
      asset = Asset.builder().status(AssetStatus.NORMAL).organisation(Organisation.DJARUM)
            .purchase(Purchase.BUY).price(100000).category(AssetCategory.MHE).dipinjam(Boolean.TRUE)
            .itemCode("ITEM-CODE").build();
      item = Item.builder().itemName("NAME").itemCode("ITEM-CODE").build();
   }

   @Test
   public void execute() {
      when(assetRepository.findByAssetNumber(anyString())).thenReturn(Mono.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(assetRepository).findByAssetNumber(anyString());
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail() {
      when(assetRepository.findByAssetNumber(anyString())).thenReturn(Mono.empty());
      command.execute(commandRequest).block();
      verify(assetRepository).findByAssetNumber(anyString());
   }
}