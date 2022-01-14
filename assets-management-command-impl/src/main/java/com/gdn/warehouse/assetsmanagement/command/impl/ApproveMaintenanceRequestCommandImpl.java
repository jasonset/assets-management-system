package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.ApproveMaintenanceRequestCommand;
import com.gdn.warehouse.assetsmanagement.command.model.ApproveMaintenanceRequestCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import com.gdn.warehouse.assetsmanagement.helper.MaintenanceHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.MaintenanceHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ApproveMaintenanceRequestCommandImpl implements ApproveMaintenanceRequestCommand {

   @Autowired
   private MaintenanceRepository maintenanceRepository;

   @Autowired
   private MaintenanceHistoryHelper maintenanceHistoryHelper;

   @Autowired
   private AssetRepository assetRepository;

   @Override
   public Mono<Boolean> execute(ApproveMaintenanceRequestCommandRequest request) {
      return maintenanceRepository.findByMaintenanceNumber(request.getMaintenanceNumber())
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Maintenance doesn't exist!", HttpStatus.BAD_REQUEST))))
            .flatMap(this::validateStatus)
            .flatMap(maintenance -> {
               if(BooleanUtils.isTrue(request.getApprove())){
                  maintenance.setStatus(MaintenanceStatus.REQUEST_RECEIVED);
               }else {
                  maintenance.setStatus(MaintenanceStatus.REQUEST_DECLINED);
               }
               maintenance.setLastModifiedDate(new Date());
               maintenance.setLastModifiedBy(request.getUsername());
               return maintenanceRepository.save(maintenance);
            }).doOnSuccess(maintenance -> {
               if (!MaintenanceStatus.REQUEST_DECLINED.equals(maintenance.getStatus())){
                  updateAssets(maintenance.getAssetNumbers(),AssetStatus.PENDING_MAINTENANCE);
               }else {
                  updateAssets(maintenance.getAssetNumbers(),AssetStatus.NORMAL);
               }
            })
            .doOnSuccess(maintenance -> maintenanceHistoryHelper.createMaintenanceHistory(toMaintenanceHistoryHelperRequest(maintenance)).subscribe())
            .flatMap(result -> Mono.just(Boolean.TRUE));
   }

   private Mono<Maintenance> validateStatus(Maintenance maintenance){
      List<MaintenanceStatus> invalidStatuses = Arrays.asList(MaintenanceStatus.SCHEDULED,MaintenanceStatus.ON_MAINTENANCE,
            MaintenanceStatus.DONE,MaintenanceStatus.REJECTED);
      if(MaintenanceStatus.REQUEST_DECLINED.equals(maintenance.getStatus())){
         return Mono.defer(()->Mono.error(new CommandErrorException("Maintenance Request "+maintenance.getMaintenanceNumber()+
               " already DECLINED",HttpStatus.BAD_REQUEST)));
      }else if(MaintenanceStatus.REQUEST_RECEIVED.equals(maintenance.getStatus())) {
         return Mono.defer(() -> Mono.error(new CommandErrorException("Maintenance Request " + maintenance.getMaintenanceNumber() +
               " already APPROVED", HttpStatus.BAD_REQUEST)));
      }else if(invalidStatuses.contains(maintenance.getStatus())){
         return Mono.defer(()->Mono.error(new CommandErrorException("Maintenance "+maintenance.getMaintenanceNumber()+
               " cannot be APPROVED/DECLINED because status in "+maintenance.getStatus().name(),HttpStatus.BAD_REQUEST)));
      }else {
         return Mono.defer(()->Mono.just(maintenance));
      }
   }

   private void updateAssets(List<String> assetNumbers, AssetStatus newStatus){
      assetRepository.findByAssetNumberIn(assetNumbers)
            .map(asset -> {
               asset.setStatus(newStatus);
               return asset;
            }).collectList()
            .flatMap(assets -> assetRepository.saveAll(assets).collectList())
            .subscribe();
   }


   private MaintenanceHistoryHelperRequest toMaintenanceHistoryHelperRequest(Maintenance maintenance){
      return MaintenanceHistoryHelperRequest.builder().maintenanceNumber(maintenance.getMaintenanceNumber())
            .maintenanceStatus(maintenance.getStatus()).updatedDate(new Date())
            .updatedBy(maintenance.getLastModifiedBy()).build();
   }
}
