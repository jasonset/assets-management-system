package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.GetAllItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.repository.custom.ItemCustomRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetAllItemCommandImplTest {
   @InjectMocks
   private GetAllItemCommandImpl command;

   @Mock
   private ItemCustomRepository itemCustomRepository;

   private GetAllItemCommandRequest commandRequest;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      commandRequest = GetAllItemCommandRequest.builder()
            .limit(1)
            .page(1)
            .nameFilter("name")
            .codeFilter("code")
            .sortBy("code")
            .sortOrder("asc").build();

      item = Item.builder()
            .itemName("name").itemCode("code").build();
   }

   @Test
   public void execute() {
      when(itemCustomRepository.findByCriteria(any(Criteria.class), any(Integer.class), any(Integer.class), any(Sort.class)))
            .thenReturn(Mono.just(new PageImpl(Arrays.asList(item), PageRequest.of(0,5),1)));
      command.execute(commandRequest).block();
      verify(itemCustomRepository).findByCriteria(any(Criteria.class), any(Integer.class), any(Integer.class), any(Sort.class));
   }

   @Test
   public void execute_filterNull_limitPageNull(){
      commandRequest.setLimit(null);
      commandRequest.setPage(null);
      commandRequest.setCodeFilter(null);
      commandRequest.setNameFilter(null);
      when(itemCustomRepository.findByCriteria(any(Criteria.class), eq(null), eq(null), any(Sort.class)))
            .thenReturn(Mono.just(new PageImpl(Arrays.asList(item), PageRequest.of(0,5),1)));
      command.execute(commandRequest).block();
      verify(itemCustomRepository).findByCriteria(any(Criteria.class), eq(null), eq(null), any(Sort.class));
   }
}