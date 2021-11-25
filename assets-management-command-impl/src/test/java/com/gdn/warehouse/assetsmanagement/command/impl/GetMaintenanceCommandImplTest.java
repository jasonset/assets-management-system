package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.request.SortByDirection;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.custom.MaintenanceCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetMaintenanceCriteriaRequest;
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

public class GetMaintenanceCommandImplTest {

   @InjectMocks
   private GetMaintenanceCommandImpl command;

   @Mock
   private MaintenanceCustomRepository maintenanceCustomRepository;

   @Mock
   private ItemRepository itemRepository;

   private GetMaintenanceCommandRequest commandRequest, commandRequest2;
   private SortBy sortBy;
   private Maintenance maintenance;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      sortBy = new SortBy();
      sortBy.setDirection(SortByDirection.ASC);
      sortBy.setPropertyName("maintenanceNumber");

      commandRequest = GetMaintenanceCommandRequest.builder()
            .assetNumberFilter("ASSET-NUMBER")
            .maintenanceNumberFilter("MAINTENANCE-NUMBER")
            .itemCodeFilter("ITEM-CODE")
            .locationFilter("LOCATION")
            .requesterFilter("USERNAME")
            .statusFilter(MaintenanceStatus.ON_MAINTENANCE.name()).sortBy(Arrays.asList(sortBy)).limit(1).page(10).build();
      commandRequest2 = GetMaintenanceCommandRequest.builder().limit(1).page(10).build();
      maintenance = Maintenance.builder().assetNumbers(Arrays.asList("ASSET-NUMBER"))
            .maintenanceNumber("MAINTENANCE-NUMBER")
            .itemCode("ITEM-CODE")
            .location("LOCATION")
            .requester("USERNAME")
            .status(MaintenanceStatus.ON_MAINTENANCE)
            .itemCode("CODE").build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute_with_filtersAndSorts() {
      when(maintenanceCustomRepository.findByCriteria(any(GetMaintenanceCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class))).thenReturn(Mono.just(new PageImpl<>(Arrays.asList(maintenance),
            PageRequest.of(0,5),1)));
      when(itemRepository.findByItemCodeIn(anyList())).thenReturn(Flux.just(item));
      command.execute(commandRequest).block();
      verify(maintenanceCustomRepository).findByCriteria(any(GetMaintenanceCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class));
      verify(itemRepository).findByItemCodeIn(anyList());
   }

   @Test
   public void execute_without_filtersAndSorts() {
      when(maintenanceCustomRepository.findByCriteria(any(GetMaintenanceCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class))).thenReturn(Mono.just(new PageImpl<>(Arrays.asList(maintenance),
            PageRequest.of(0,5),1)));
      when(itemRepository.findByItemCodeIn(anyList())).thenReturn(Flux.just(item));
      command.execute(commandRequest2).block();
      verify(maintenanceCustomRepository).findByCriteria(any(GetMaintenanceCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class));
      verify(itemRepository).findByItemCodeIn(anyList());
   }
}