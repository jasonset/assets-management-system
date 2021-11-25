package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.entity.MaintenanceHistory;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.helper.model.MaintenanceHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceHistoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MaintenanceHistoryHelperImplTest {

   @InjectMocks
   private MaintenanceHistoryHelperImpl helper;

   @Mock
   private MaintenanceHistoryRepository maintenanceHistoryRepository;

   private MaintenanceHistoryHelperRequest request;
   private MaintenanceHistory maintenanceHistory;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      request = MaintenanceHistoryHelperRequest.builder()
            .maintenanceNumber("MT-NUMBER")
            .maintenanceStatus(MaintenanceStatus.DONE)
            .updatedDate(new Date())
            .updatedBy("username").build();

      maintenanceHistory = MaintenanceHistory.builder().build();
   }

   @Test
   public void createMaintenanceHistory() {
      when(maintenanceHistoryRepository.save(any(MaintenanceHistory.class))).thenReturn(Mono.just(maintenanceHistory));
      helper.createMaintenanceHistory(request).block();
      verify(maintenanceHistoryRepository).save(any(MaintenanceHistory.class));
   }
}