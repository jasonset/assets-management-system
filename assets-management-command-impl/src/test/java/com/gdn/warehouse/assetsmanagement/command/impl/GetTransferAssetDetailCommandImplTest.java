package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.TransferAssetRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetTransferAssetDetailCommandImplTest {

   @InjectMocks
   private GetTransferAssetDetailCommandImpl command;

   @Mock
   private TransferAssetRepository transferAssetRepository;

   @Mock
   private ItemRepository itemRepository;

   private GetTransferAssetDetailCommandRequest commandRequest;
   private TransferAsset transferAsset;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = GetTransferAssetDetailCommandRequest.builder().transferAssetNumber("TA-NUMBER").build();
      transferAsset = TransferAsset.builder()
            .transferAssetNumber("TA-NUMBER")
            .assetNumbers(Collections.singletonList("ASSET-NUMBER"))
            .arrivalDate(new Date())
            .deliveryDate(new Date())
            .itemCode("ITEM-CODE")
            .referenceNumber("TA-123")
            .transferAssetType(TransferAssetType.RETURN)
            .origin("ORIGIN")
            .destination("DESTINATION")
            .status(TransferAssetStatus.PENDING)
            .notes("NOTES").build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute_success() {
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.just(transferAsset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail() {
      when(transferAssetRepository.findByTransferAssetNumber(anyString())).thenReturn(Mono.empty());
      command.execute(commandRequest).block();
      verify(transferAssetRepository).findByTransferAssetNumber(anyString());
   }
}