package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.GetMaintenanceHistoryCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceHistoryCommandRequest;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceHistoryRepository;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceHistoryWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class GetMaintenanceHistoryCommandImpl implements GetMaintenanceHistoryCommand {
   @Autowired
   private MaintenanceHistoryRepository maintenanceHistoryRepository;

   @Override
   public Mono<List<GetMaintenanceHistoryWebResponse>> execute(GetMaintenanceHistoryCommandRequest request) {
      return maintenanceHistoryRepository.findByMaintenanceNumberOrderByUpdatedDateAsc(request.getMaintenanceNumber())
            .flatMap(maintenanceHistory -> {
               GetMaintenanceHistoryWebResponse response = GetMaintenanceHistoryWebResponse.builder().build();
               BeanUtils.copyProperties(maintenanceHistory,response);
               response.setStatus(maintenanceHistory.getStatus().name());
               return Mono.just(response);
            }).collectList();
   }
}
