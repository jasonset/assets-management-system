package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.RejectMaintenanceCommand;
import com.gdn.warehouse.assetsmanagement.command.model.RejectMaintenanceCommandRequest;
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
public class RejectMaintenanceCommandImpl implements RejectMaintenanceCommand {

   @Autowired
   private MaintenanceRepository maintenanceRepository;

   @Autowired
   private MaintenanceHistoryHelper maintenanceHistoryHelper;

   @Autowired
   private AssetRepository assetRepository;

   @Override
   public Mono<Boolean> execute(RejectMaintenanceCommandRequest request) {
      return maintenanceRepository.findByMaintenanceNumber(request.getMaintenanceNumber())
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Maintenance doesn't exist!", HttpStatus.BAD_REQUEST))))
            .flatMap(this::validateStatus)
            .flatMap(maintenance -> {
               maintenance.setStatus(MaintenanceStatus.REJECTED);
               maintenance.setAlasanReject(request.getAlasanReject());
               maintenance.setLastModifiedBy(request.getUsername());
               maintenance.setLastModifiedDate(new Date());
               return maintenanceRepository.save(maintenance);
            }).flatMap(maintenance -> assetRepository.findByAssetNumberIn(maintenance.getAssetNumbers()).collectList()
                  .flatMap(assets -> {
                     assets.forEach(asset -> asset.setStatus(AssetStatus.NORMAL));
                     return assetRepository.saveAll(assets).collectList();
                  }).map(result -> maintenance))
            .flatMap(maintenance -> maintenanceHistoryHelper.createMaintenanceHistory(toMaintenanceHistoryHelperRequest(maintenance)))
            .map(result -> Boolean.TRUE);
   }

   private Mono<Maintenance> validateStatus(Maintenance maintenance){
      List<MaintenanceStatus> invalidStatuses = Arrays.asList(MaintenanceStatus.REQUEST_PENDING,MaintenanceStatus.REQUEST_DECLINED,
            MaintenanceStatus.DONE);
      if(MaintenanceStatus.REJECTED.equals(maintenance.getStatus())){
         return Mono.defer(()->Mono.error(new CommandErrorException("Maintenance "+maintenance.getMaintenanceNumber()+
               " already REJECTED",HttpStatus.BAD_REQUEST)));
      }else if(invalidStatuses.contains(maintenance.getStatus())){
         return Mono.defer(()->Mono.error(new CommandErrorException("Maintenance "+maintenance.getMaintenanceNumber()+
               " cannot be REJECTED because the status is "+maintenance.getStatus().name(),HttpStatus.BAD_REQUEST)));
      }else {
         return Mono.defer(()->Mono.just(maintenance));
      }
   }

   private MaintenanceHistoryHelperRequest toMaintenanceHistoryHelperRequest(Maintenance maintenance){
      return MaintenanceHistoryHelperRequest.builder().maintenanceNumber(maintenance.getMaintenanceNumber())
            .maintenanceStatus(maintenance.getStatus()).updatedDate(new Date()).updatedBy(maintenance.getLastModifiedBy()).build();
   }
}
