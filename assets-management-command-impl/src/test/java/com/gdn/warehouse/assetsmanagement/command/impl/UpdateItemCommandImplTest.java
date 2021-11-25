package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.UpdateItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class UpdateItemCommandImplTest {
   @InjectMocks
   private UpdateItemCommandImpl command;

   @Mock
   private ItemRepository itemRepository;

   @Mock
   private AssetRepository assetRepository;

   private UpdateItemCommandRequest commandRequest;
   private Item item;
   private Asset asset;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = UpdateItemCommandRequest.builder()
            .itemCode("CODE").itemName("NAME").category("MHE")
            .username("USERNAME").build();
      item = Item.builder().itemCode("CODE").itemName("NAME").category(AssetCategory.MHE).build();
      asset = Asset.builder().build();
   }

   @Test
   public void execute_success_same_category() {
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(itemRepository).findByItemCode(anyString());
      verify(itemRepository).save(any(Item.class));
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail_have_asset() {
      commandRequest.setCategory("SHR");
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(assetRepository.existsByItemCode(anyString())).thenReturn(Mono.just(Boolean.TRUE));
      when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(itemRepository).findByItemCode(anyString());
      verify(itemRepository).save(any(Item.class));
      verify(assetRepository).existsByItemCode(anyString());
   }

   @Test
   public void execute_fail_not_have_asset() {
      commandRequest.setCategory("SHR");
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      when(assetRepository.existsByItemCode(anyString())).thenReturn(Mono.just(Boolean.FALSE));
      when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(itemRepository).findByItemCode(anyString());
      verify(itemRepository).save(any(Item.class));
      verify(assetRepository).existsByItemCode(anyString());
   }
}