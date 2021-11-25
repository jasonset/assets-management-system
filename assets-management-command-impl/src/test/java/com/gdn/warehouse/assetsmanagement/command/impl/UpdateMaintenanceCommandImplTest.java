package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.model.UpdateMaintenanceCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.helper.MaintenanceHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.MaintenanceHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateMaintenanceCommandImplTest {

   @InjectMocks
   private UpdateMaintenanceCommandImpl command;

   @Mock
   private MaintenanceRepository maintenanceRepository;

   @Mock
   private MaintenanceHistoryHelper maintenanceHistoryHelper;

   @Mock
   private AssetRepository assetRepository;

   @Mock
   private ItemRepository itemRepository;

   @Mock
   private SendEmailHelper sendEmailHelper;

   private UpdateMaintenanceCommandRequest commandRequest;
   private Maintenance maintenance;
   private Asset asset;
   private Item item;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);

      commandRequest = UpdateMaintenanceCommandRequest.builder()
            .maintenanceNumber("MT-NUMBER")
            .maintenanceFee(200000)
            .tanggalKerusakan(1L)
            .deskripsiKerusakan("DESKRIPSI")
            .quoSubmit(1L)
            .poSubmit(1L)
            .tanggalNormal(null)
            .tanggalService(null)
            .notes("NOTES")
            .username("username")
            .build();
      maintenance = Maintenance.builder().assetNumbers(Arrays.asList("ASSET-NUMBER"))
            .itemCode("CODE").status(MaintenanceStatus.REQUEST_RECEIVED).build();
      asset = Asset.builder().build();
      item = Item.builder().itemName("NAME").itemCode("CODE").build();
   }

   @Test
   public void execute() {
      when(maintenanceRepository.findByMaintenanceNumber("MT-NUMBER")).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber("MT-NUMBER");
      verify(maintenanceRepository).save(any(Maintenance.class));
   }

   @Test(expected = CommandErrorException.class)
   public void execute_fail() {
      when(maintenanceRepository.findByMaintenanceNumber("MT-NUMBER")).thenReturn(Mono.empty());
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber("MT-NUMBER");
   }

   @Test
   public void execute_tanggalService() {
      commandRequest.setTanggalService(1L);
      when(maintenanceHistoryHelper.createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      when(maintenanceRepository.findByMaintenanceNumber("MT-NUMBER")).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber("MT-NUMBER");
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(maintenanceHistoryHelper).createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(itemRepository).findByItemCode(anyString());
   }

   @Test
   public void execute_tanggalService_already_on_maintenance() {
      commandRequest.setTanggalService(1L);
      maintenance.setStatus(MaintenanceStatus.ON_MAINTENANCE);
      when(maintenanceRepository.findByMaintenanceNumber("MT-NUMBER")).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber("MT-NUMBER");
      verify(maintenanceRepository).save(any(Maintenance.class));
   }

   @Test
   public void execute_tanggalNormal() {
      commandRequest.setTanggalNormal(1L);
      commandRequest.setTanggalService(1L);
      when(maintenanceHistoryHelper.createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class)))
            .thenReturn(Mono.just(Boolean.TRUE));
      when(maintenanceRepository.findByMaintenanceNumber("MT-NUMBER")).thenReturn(Mono.just(maintenance));
      when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(Mono.just(maintenance));
      when(assetRepository.findByAssetNumberIn(anyList())).thenReturn(Flux.just(asset));
      when(assetRepository.saveAll(anyList())).thenReturn(Flux.just(asset));
      when(itemRepository.findByItemCode(anyString())).thenReturn(Mono.just(item));
      command.execute(commandRequest).block();
      verify(maintenanceRepository).findByMaintenanceNumber("MT-NUMBER");
      verify(maintenanceRepository).save(any(Maintenance.class));
      verify(maintenanceHistoryHelper).createMaintenanceHistory(any(MaintenanceHistoryHelperRequest.class));
      verify(assetRepository).findByAssetNumberIn(anyList());
      verify(assetRepository).saveAll(anyList());
      verify(itemRepository).findByItemCode(anyString());
   }

}