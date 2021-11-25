package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.response.Paging;
import com.gdn.warehouse.assetsmanagement.command.GetMaintenanceCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.custom.MaintenanceCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetMaintenanceCriteriaRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GetMaintenanceCommandImpl implements GetMaintenanceCommand {

   @Autowired
   private MaintenanceCustomRepository maintenanceCustomRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<Pair<List<GetMaintenanceWebResponse>,Paging>> execute(GetMaintenanceCommandRequest request) {
      return maintenanceCustomRepository.findByCriteria(constructCriteriaRequest(request), request.getLimit(), request.getPage(),
            constructSort(request.getSortBy()))
            .flatMap(maintenances -> getAllItems(maintenances.getContent())
                  .map(itemMap -> Pair.of(toGetMaintenanceWebResponse(maintenances.getContent(),itemMap),getPaginationForMaintenance(maintenances))));
   }

   private Sort constructSort(List<SortBy> sortBy){
      return CollectionUtils.isEmpty(sortBy)?
            Sort.by(Sort.Direction.fromString(StringConstants.DEFAULT_SORT_DIRECTION),"maintenanceNumber"):
            Sort.by(sortBy.stream()
                  .map(sort -> new Sort.Order(Sort.Direction.fromString(sort.getDirection().name()), sort.getPropertyName()))
                  .collect(Collectors.toList()));
   }

   private GetMaintenanceCriteriaRequest constructCriteriaRequest(GetMaintenanceCommandRequest request){
      return GetMaintenanceCriteriaRequest.builder()
            .maintenanceNumberFilter(request.getMaintenanceNumberFilter())
            .assetNumberFilter(request.getAssetNumberFilter())
            .requesterFilter(request.getRequesterFilter())
            .itemCodeFilter(request.getItemCodeFilter())
            .locationFilter(request.getLocationFilter())
            .tanggalLaporanStartFilter(request.getTanggalLaporanStartFilter()==null?null:new Date(request.getTanggalLaporanStartFilter()))
            .tanggalLaporanEndFilter(request.getTanggalLaporanEndFilter()==null?null:new Date(request.getTanggalLaporanEndFilter()))
            .statusFilter(request.getStatusFilter()).build();
   }

   private Mono<Map<String,String>> getAllItems(List<Maintenance> maintenances){
      List<String> itemCodes = maintenances.stream().map(Maintenance::getItemCode).distinct().collect(Collectors.toList());
      return itemRepository.findByItemCodeIn(itemCodes).collectMap(Item::getItemCode,Item::getItemName);
   }

   private List<GetMaintenanceWebResponse> toGetMaintenanceWebResponse(List<Maintenance> maintenances, Map<String,String> itemMap){
      return maintenances.stream().map(maintenance ->
            GetMaintenanceWebResponse.builder().maintenanceNumber(maintenance.getMaintenanceNumber())
                  .assetNumbers(maintenance.getAssetNumbers())
                  .requester(maintenance.getRequester())
                  .itemName(itemMap.get(maintenance.getItemCode()))
                  .location(maintenance.getLocation())
                  .tanggalLaporan(maintenance.getTanggalLaporan())
                  .status(maintenance.getStatus().name()).build()).collect(Collectors.toList());
   }

   private Paging getPaginationForMaintenance(Page<Maintenance> maintenances){
      return Paging.builder().itemPerPage((long)maintenances.getSize())
            .page((long)maintenances.getNumber()+1)
            .totalItem(maintenances.getTotalElements())
            .totalPage((long)maintenances.getTotalPages()).build();
   }
}
