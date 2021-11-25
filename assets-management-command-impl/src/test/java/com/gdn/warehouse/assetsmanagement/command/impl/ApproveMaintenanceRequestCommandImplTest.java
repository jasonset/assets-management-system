package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.ApproveMaintenanceRequestCommandRequest;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApproveMaintenanceRequestCommandImplTest {
   @InjectMocks
   private ApproveMaintenanceRequestCommandImpl command;

   @Mock
   private MaintenanceRepository maintenanceRepository;

   @Mock
   private MaintenanceHistoryHelper maintenanceHistoryHelper;

   @Mock
   private AssetRepository assetRepository;

   private ApproveMaintenanceRequestCommandRequest commandRequest;
   private Maintenance maintenance;
   private Asset asset;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = ApproveMaintenanceRequestCommandRequest.builder()
            .maintenanceNumber("CODE")
            .username("username")
            .approve(Boolean.TRUE).build();

      maintenance = Maintenance.builder().assetNumbers(Arrays.asList("ASSET_NUMBER")).build();
      asset = Asset.builder().assetNumber("ASSET_NUMBER").build();
   }

   @Test
   public void execute_true() {
      when(maintenanceRepository.findByMaintenanceNumber(anyString())).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(maintenanceHistoryHelper.createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber(anyString());
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(maintenanceHistoryHelper).createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_true_request_received() {
      maintenance.setStatus(MaintenanceStatus.REQUEST_RECEIVED);
      when(maintenanceRepository.findByMaintenanceNumber(anyString())).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(maintenanceHistoryHelper.createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber(anyString());
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(maintenanceHistoryHelper).createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_true_request_declined() {
      maintenance.setStatus(MaintenanceStatus.REQUEST_DECLINED);
      when(maintenanceRepository.findByMaintenanceNumber(anyString())).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(maintenanceHistoryHelper.createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber(anyString());
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(maintenanceHistoryHelper).createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_true_scheduled() {
      maintenance.setStatus(MaintenanceStatus.SCHEDULED);
      when(maintenanceRepository.findByMaintenanceNumber(anyString())).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(maintenanceHistoryHelper.createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber(anyString());
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(maintenanceHistoryHelper).createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
   }

   @Test
   public void execute_false() {
      commandRequest.setApprove(Boolean.FALSE);
      when(maintenanceRepository.findByMaintenanceNumber(anyString())).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(maintenanceHistoryHelper.createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber(anyString());
      verify(maintenanceHistoryHelper).createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class));
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
   }

   @Test(expected = CommandErrorException.class)
   public void execute_false_fail() {
      commandRequest.setApprove(Boolean.FALSE);
      when(maintenanceRepository.findByMaintenanceNumber(anyString())).thenReturn(Mono.empty());
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber(anyString());
   }
}