package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceHistoryCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceHistory;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceHistoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class GetMaintenanceHistoryCommandImplTest {
   @InjectMocks
   private GetMaintenanceHistoryCommandImpl command;

   @Mock
   private MaintenanceHistoryRepository maintenanceHistoryRepository;

   private MaintenanceHistory maintenanceHistory,maintenanceHistory2;
   private GetMaintenanceHistoryCommandRequest commandRequest;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      maintenanceHistory = MaintenanceHistory.builder().status(MaintenanceStatus.REQUEST_RECEIVED).build();
      maintenanceHistory2 = MaintenanceHistory.builder().status(MaintenanceStatus.ON_MAINTENANCE).build();
      commandRequest = GetMaintenanceHistoryCommandRequest.builder().maintenanceNumber("MAINTENANCE-NUMBER").build();
   }

   @Test
   public void execute() {
      when(maintenanceHistoryRepository.findByMaintenanceNumberOrderByUpdatedDateAsc(anyString()))
            .thenReturn(Flux.just(maintenanceHistory,maintenanceHistory2));
      command.execute(commandRequest).block();
      verify(maintenanceHistoryRepository).findByMaintenanceNumberOrderByUpdatedDateAsc(anyString());
   }
}