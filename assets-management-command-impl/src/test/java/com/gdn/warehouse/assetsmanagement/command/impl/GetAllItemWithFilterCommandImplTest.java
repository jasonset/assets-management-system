package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.request.SortByDirection;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllItemWithFilterCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.repository.custom.ItemCustomRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAllItemWithFilterCommandImplTest {
   @InjectMocks
   private GetAllItemWithFilterCommandImpl command;

   @Mock
   private ItemCustomRepository itemCustomRepository;

   private SortBy sortBy;
   private GetAllItemWithFilterCommandRequest commandRequest,commandRequest2;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      sortBy = new SortBy();
      sortBy.setDirection(SortByDirection.ASC);
      sortBy.setPropertyName("code");
      commandRequest = GetAllItemWithFilterCommandRequest.builder()
            .codeFilter("code")
            .categoryFilter("MHE")
            .nameFilter("name")
            .limit(1)
            .page(10)
            .sortBy(Arrays.asList(sortBy)).build();
      commandRequest2 = GetAllItemWithFilterCommandRequest.builder().limit(1).page(10).build();
      item = Item.builder().itemCode("code").itemName("name").category(AssetCategory.MHE).build();
   }

   @Test
   public void execute_sort() {
      when(itemCustomRepository.findByCriteria(any(Criteria.class),any(Integer.class),any(Integer.class),any(Sort.class)))
            .thenReturn(Mono.just(new PageImpl<>(Arrays.asList(item), PageRequest.of(0,5),1)));
      command.execute(commandRequest).block();
      verify(itemCustomRepository).findByCriteria(any(Criteria.class),any(Integer.class),any(Integer.class),any(Sort.class));
   }

   @Test
   public void execute_without_sort() {
      when(itemCustomRepository.findByCriteria(any(Criteria.class),any(Integer.class),any(Integer.class),any(Sort.class)))
            .thenReturn(Mono.just(new PageImpl<>(Arrays.asList(item), PageRequest.of(0,5),1)));
      command.execute(commandRequest2).block();
      verify(itemCustomRepository).findByCriteria(any(Criteria.class),any(Integer.class),any(Integer.class),any(Sort.class));
   }
}