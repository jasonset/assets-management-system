package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.request.SortByDirection;
import com.gdn.warehouse.assetsmanagement.command.model.GetAssetCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.Organisation;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.custom.AssetCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetAssetCriteriaRequest;
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

public class GetAssetCommandImplTest {

   @InjectMocks
   private GetAssetCommandImpl command;

   @Mock
   private AssetCustomRepository assetCustomRepository;

   @Mock
   private ItemRepository itemRepository;

   private GetAssetCommandRequest commandRequest,commandRequest2;
   private SortBy sortBy;
   private Asset asset;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      sortBy = new SortBy();
      sortBy.setDirection(SortByDirection.ASC);
      sortBy.setPropertyName("assetNumber");
      commandRequest = GetAssetCommandRequest.builder()
            .assetNumberFilter("ASSET-NUMBER").organisationFilter("GDN").vendorFilter("VENDOR")
            .itemCodeFilter("CODE").locationFilter("LOCATION").statusFilter("NORMAL")
            .sortBy(Arrays.asList(sortBy)).limit(1).page(10).build();
      commandRequest2 = GetAssetCommandRequest.builder().limit(1).page(10).build();
      asset = Asset.builder().assetNumber("ASSET-NUMBER").organisation(Organisation.GDN).vendor("VENDOR")
            .itemCode("CODE").location("LOCATION").status(AssetStatus.NORMAL).build();
      item = Item.builder().itemCode("CODE").itemName("NAME").build();
   }

   @Test
   public void execute_with_filtersAndSorts() {
      when(assetCustomRepository.findByCriteria(any(GetAssetCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class))).thenReturn(Mono.just(new PageImpl<>(Arrays.asList(asset),
            PageRequest.of(0,5),1)));
      when(itemRepository.findByItemCodeIn(anyList())).thenReturn(Flux.just(item));
      command.execute(commandRequest).block();
      verify(assetCustomRepository).findByCriteria(any(GetAssetCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class));
      verify(itemRepository).findByItemCodeIn(anyList());
   }

   @Test
   public void execute_without_filtersAndSorts() {
      when(assetCustomRepository.findByCriteria(any(GetAssetCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class))).thenReturn(Mono.just(new PageImpl<>(Arrays.asList(asset),
            PageRequest.of(0,5),1)));
      when(itemRepository.findByItemCodeIn(anyList())).thenReturn(Flux.just(item));
      command.execute(commandRequest2).block();
      verify(assetCustomRepository).findByCriteria(any(GetAssetCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class));
      verify(itemRepository).findByItemCodeIn(anyList());
   }
}