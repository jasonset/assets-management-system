package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.RejectMaintenanceCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.helper.MaintenanceHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.MaintenanceHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class RejectMaintenanceCommandImplTest {
   @InjectMocks
   private RejectMaintenanceCommandImpl command;

   @Mock
   private MaintenanceRepository maintenanceRepository;

   @Mock
   private MaintenanceHistoryHelper maintenanceHistoryHelper;

   @Mock
   private AssetRepository assetRepository;

   private RejectMaintenanceCommandRequest commandRequest;
   private Maintenance maintenance;
   private Asset asset;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = RejectMaintenanceCommandRequest.builder().maintenanceNumber("MT-NUMBER")
            .alasanReject("NOT BROKEN").username("username").build();

      maintenance = Maintenance.builder().assetNumbers(Arrays.asList("ASSET-NUMBER")).status(MaintenanceStatus.ON_MAINTENANCE).build();
      asset = Asset.builder().assetNumber("ASSET-NUMBER").build();
   }

   @Test
   public void execute() {
      when(maintenanceRepository.findByMaintenanceNumber("MT-NUMBER")).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(maintenanceHistoryHelper.createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber("MT-NUMBER");
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(maintenanceHistoryHelper).createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());

   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail() {
      when(maintenanceRepository.findByMaintenanceNumber("MT-NUMBER")).thenReturn(Mono.empty());
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber("MT-NUMBER");
   }

   @Test(expected = CommandErrorException.class)
   public void execute_request_pending() {
      maintenance.setStatus(MaintenanceStatus.REQUEST_PENDING);
      when(maintenanceRepository.findByMaintenanceNumber("MT-NUMBER")).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(maintenanceHistoryHelper.createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber("MT-NUMBER");
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(maintenanceHistoryHelper).createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class));
   }

   @Test(expected = CommandErrorException.class)
   public void execute_rejected() {
      maintenance.setStatus(MaintenanceStatus.REJECTED);
      when(maintenanceRepository.findByMaintenanceNumber("MT-NUMBER")).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(maintenanceHistoryHelper.createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber("MT-NUMBER");
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(maintenanceHistoryHelper).createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class));
   }
}