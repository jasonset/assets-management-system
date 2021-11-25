package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.GetMaintenanceDetailCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceDetailCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.MaintenanceRepository;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceDetailWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class GetMaintenanceDetailCommandImpl implements GetMaintenanceDetailCommand {

   @Autowired
   private MaintenanceRepository maintenanceRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<GetMaintenanceDetailWebResponse> execute(GetMaintenanceDetailCommandRequest request) {
      return maintenanceRepository.findByMaintenanceNumber(request.getMaintenanceNumber())
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Maintenance doesn't exist!", HttpStatus.BAD_REQUEST))))
            .flatMap(maintenance -> itemRepository.findByItemCode(maintenance.getItemCode())
                  .flatMap(item -> {
                     GetMaintenanceDetailWebResponse response = GetMaintenanceDetailWebResponse.builder().build();
                     BeanUtils.copyProperties(maintenance,response);
                     response.setStatus(maintenance.getStatus().name());
                     response.setItemName(item.getItemName());
                     return Mono.just(response);
                  }));
   }
}
