package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.request.SortByDirection;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.custom.MaintenanceReminderCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetMaintenanceReminderCriteriaRequest;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetMaintenanceReminderCommandImplTest {
   
   @InjectMocks
   private GetMaintenanceReminderCommandImpl command;
   
   @Mock
   private MaintenanceReminderCustomRepository maintenanceReminderCustomRepository;

   @Mock
   private ItemRepository itemRepository;
   
   private GetMaintenanceReminderCommandRequest commandRequest,commandRequest2;
   private SortBy sortBy,sortBy2;
   private MaintenanceReminder maintenanceReminder;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      sortBy = new SortBy();
      sortBy.setDirection(SortByDirection.ASC);
      sortBy.setPropertyName("maintenanceReminderNumber");

      sortBy2 = new SortBy();
      sortBy2.setDirection(SortByDirection.ASC);
      sortBy2.setPropertyName("scheduledDate");

      commandRequest = GetMaintenanceReminderCommandRequest.builder()
            .maintenanceReminderNumberFilter("MR-NUMBER")
            .assetNumberFilter("ASSET-NUMBER")
            .itemCodeFilter("ITEM-CODE")
            .emailFilter("EMAIL")
            .intervalFilter(1)
            .sortBy(Arrays.asList(sortBy,sortBy2)).limit(1).page(10).build();
      commandRequest2 = GetMaintenanceReminderCommandRequest.builder().limit(1).page(10).build();
      maintenanceReminder = MaintenanceReminder.builder()
            .maintenanceReminderNumber("MR-NUMBER")
            .assetNumbers(Arrays.asList("ASSET-NUMBER"))
            .itemCode("ITEM-CODE")
            .emailList(Arrays.asList("EMAIL"))
            .interval(1).build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute_with_filtersAndSorts() {
      when(maintenanceReminderCustomRepository.findByCriteria(any(GetMaintenanceReminderCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class))).thenReturn(Mono.just(new PageImpl<>(Arrays.asList(maintenanceReminder),
            PageRequest.of(0,5),1)));
      when(itemRepository.findByItemCodeIn(anyList())).thenReturn(Flux.just(item));
      command.execute(commandRequest).block();
      verify(maintenanceReminderCustomRepository).findByCriteria(any(GetMaintenanceReminderCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class));
      verify(itemRepository).findByItemCodeIn(anyList());
   }

   @Test
   public void execute_without_filtersAndSorts() {
      when(maintenanceReminderCustomRepository.findByCriteria(any(GetMaintenanceReminderCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class))).thenReturn(Mono.just(new PageImpl<>(Arrays.asList(maintenanceReminder),
            PageRequest.of(0,5),1)));
      when(itemRepository.findByItemCodeIn(anyList())).thenReturn(Flux.just(item));
      command.execute(commandRequest2).block();
      verify(maintenanceReminderCustomRepository).findByCriteria(any(GetMaintenanceReminderCriteriaRequest.class),any(Integer.class),
            any(Integer.class),any(Sort.class));
      verify(itemRepository).findByItemCodeIn(anyList());
   }
}