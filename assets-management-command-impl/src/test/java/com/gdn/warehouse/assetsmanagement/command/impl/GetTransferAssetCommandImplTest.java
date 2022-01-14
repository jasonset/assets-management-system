package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.request.SortByDirection;
import com.gdn.warehouse.assetsmanagement.command.model.GetTransferAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.custom.TransferAssetCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetTransferAssetCriteriaRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetTransferAssetCommandImplTest {

   @InjectMocks
   private GetTransferAssetCommandImpl command;

   @Mock
   private TransferAssetCustomRepository transferAssetCustomRepository;

   @Mock
   private ItemRepository itemRepository;

   private GetTransferAssetCommandRequest commandRequest,commandRequest2;
   private SortBy sortBy;
   private TransferAsset transferAsset;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      sortBy = new SortBy();
      sortBy.setDirection(SortByDirection.ASC);
      sortBy.setPropertyName("transferAssetNumber");
      commandRequest = GetTransferAssetCommandRequest.builder()
            .transferAssetNumberFilter("TRANSFER-ASSET-NUMBER")
            .originFilter("ORIGIN")
            .destinationFilter("DESTINATION")
            .itemCodeFilter("ITEM-CODE")
            .assetNumberFilter("ASSET-NUMBER")
            .statusFilter(TransferAssetStatus.PENDING.name())
            .referenceNumberFilter("REFERENCE")
            .transferAssetTypeFilter(TransferAssetType.MOVE.name())
            .sortBy(Arrays.asList(sortBy))
            .limit(1).page(10).build();
      commandRequest2 = GetTransferAssetCommandRequest.builder().limit(1).page(10).build();
      transferAsset = TransferAsset.builder()
            .transferAssetNumber("TRANSFER-ASSET-NUMBER")
            .assetNumbers(Arrays.asList("ASSET-NUMBER"))
            .origin("ORIGIN")
            .destination("DESTINATION")
            .itemCode("ITEM-CODE")
            .status(TransferAssetStatus.PENDING)
            .referenceNumber("REFERENCE")
            .transferAssetType(TransferAssetType.MOVE)
            .build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute_with_filtersAndSorts() {
      when(transferAssetCustomRepository.findByCriteria(any(GetTransferAssetCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class))).thenReturn(Mono.just(new PageImpl<>(Arrays.asList(transferAsset),
            PageRequest.of(0,5),1)));
      when(itemRepository.findByItemCodeIn(anyList())).thenReturn(Flux.just(item));
      command.execute(commandRequest).block();
      verify(transferAssetCustomRepository).findByCriteria(any(GetTransferAssetCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class));
      verify(itemRepository).findByItemCodeIn(anyList());
   }

   @Test
   public void execute_without_filtersAndSorts() {
      when(transferAssetCustomRepository.findByCriteria(any(GetTransferAssetCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class))).thenReturn(Mono.just(new PageImpl<>(Arrays.asList(transferAsset),
            PageRequest.of(0,5),1)));
      when(itemRepository.findByItemCodeIn(anyList())).thenReturn(Flux.just(item));
      command.execute(commandRequest2).block();
      verify(transferAssetCustomRepository).findByCriteria(any(GetTransferAssetCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class));
      verify(itemRepository).findByItemCodeIn(anyList());
   }
}