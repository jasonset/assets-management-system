package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.GetAllWarehouseCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import com.gdn.warehouse.assetsmanagement.repository.custom.WarehouseCustomRepository;
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

public class GetAllWarehouseCommandImplTest {
   @InjectMocks
   private GetAllWarehouseCommandImpl command;

   @Mock
   private WarehouseCustomRepository warehouseCustomRepository;

   private GetAllWarehouseCommandRequest commandRequest;
   private Warehouse warehouse;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      commandRequest = GetAllWarehouseCommandRequest.builder()
            .limit(1)
            .page(1)
            .nameFilter("name")
            .codeFilter("code")
            .sortBy("code")
            .sortOrder("asc").build();

      warehouse = Warehouse.builder()
            .warehouseName("name").warehouseCode("code").build();
   }

   @Test
   public void execute() {
      when(warehouseCustomRepository.findByCriteria(any(Criteria.class), any(Integer.class), any(Integer.class), any(Sort.class)))
            .thenReturn(Mono.just(new PageImpl(Arrays.asList(warehouse), PageRequest.of(0,5),1)));
      command.execute(commandRequest).block();
      verify(warehouseCustomRepository).findByCriteria(any(Criteria.class), any(Integer.class), any(Integer.class), any(Sort.class));
   }

   @Test
   public void execute_filterNull_limitPageNull(){
      commandRequest.setLimit(null);
      commandRequest.setPage(null);
      commandRequest.setCodeFilter(null);
      commandRequest.setNameFilter(null);
      when(warehouseCustomRepository.findByCriteria(any(Criteria.class), eq(null), eq(null), any(Sort.class)))
            .thenReturn(Mono.just(new PageImpl(Arrays.asList(warehouse), PageRequest.of(0,5),1)));
      command.execute(commandRequest).block();
      verify(warehouseCustomRepository).findByCriteria(any(Criteria.class), eq(null), eq(null), any(Sort.class));
   }
}