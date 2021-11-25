package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetMaintenanceDetailCommandImplTest {

   @InjectMocks
   private GetMaintenanceDetailCommandImpl command;

   @Mock
   private MaintenanceRepository maintenanceRepository;

   @Mock
   private ItemRepository itemRepository;

   private GetMaintenanceDetailCommandRequest commandRequest;
   private Maintenance maintenance;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = GetMaintenanceDetailCommandRequest.builder().maintenanceNumber("MAINTENANCE-NUMBER").build();
      maintenance = Maintenance.builder().itemCode("CODE").status(MaintenanceStatus.REQUEST_RECEIVED).build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute() {
      when(maintenanceRepository.findByMaintenanceNumber(anyString())).thenReturn(Mono.just(maintenance));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber(anyString());
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail() {
      when(maintenanceRepository.findByMaintenanceNumber(anyString())).thenReturn(Mono.empty());
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber(anyString());
   }
}