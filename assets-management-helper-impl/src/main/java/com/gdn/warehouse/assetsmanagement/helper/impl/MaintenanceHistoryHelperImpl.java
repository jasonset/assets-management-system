package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.entity.MaintenanceHistory;
import com.gdn.warehouse.assetsmanagement.helper.MaintenanceHistoryHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.MaintenanceHistoryHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MaintenanceHistoryHelperImpl implements MaintenanceHistoryHelper {

   @Autowired
   private MaintenanceHistoryRepository maintenanceHistoryRepository;

   @Override
   public Mono<Boolean> createMaintenanceHistory(MaintenanceHistoryHelperRequest request) {
      return maintenanceHistoryRepository.save(MaintenanceHistory.builder()
            .maintenanceNumber(request.getMaintenanceNumber()).status(request.getMaintenanceStatus())
            .updatedDate(request.getUpdatedDate()).updatedBy(request.getUpdatedBy()).build())
            .map(result -> Boolean.TRUE);
   }
}
