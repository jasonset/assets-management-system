package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.response.Paging;
import com.gdn.warehouse.assetsmanagement.command.GetAllWarehouseCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllWarehouseCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import com.gdn.warehouse.assetsmanagement.repository.custom.WarehouseCustomRepository;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetWarehouseWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GetAllWarehouseCommandImpl implements GetAllWarehouseCommand {

   @Autowired
   private WarehouseCustomRepository warehouseCustomRepository;

   @Override
   public Mono<Pair<List<GetWarehouseWebResponse>, Paging>> execute(GetAllWarehouseCommandRequest request) {
      return mono(()-> constructCriteria(request))
            .flatMap(criteria -> warehouseCustomRepository.findByCriteria(criteria,request.getLimit(), request.getPage(),
                  constructSort(request)))
            .map(warehouses -> Pair.of(toGetWarehouseWebResponses(warehouses.getContent()),getPagingForWarehouse(warehouses)));
   }

   private Criteria constructCriteria(GetAllWarehouseCommandRequest request){
      Criteria criteria = new Criteria();
      if(StringUtils.isNotEmpty(request.getCodeFilter())){
         criteria = criteria.and("warehouseCode").regex(request.getCodeFilter());
      }

      if(StringUtils.isNotEmpty(request.getNameFilter())){
         criteria = criteria.and("warehouseName").regex(request.getNameFilter(),"i");
      }

      return criteria;
   }

   private Sort constructSort(GetAllWarehouseCommandRequest request){
      return Sort.by(Sort.Direction.fromString(request.getSortOrder()),request.getSortBy());
   }

   private Paging getPagingForWarehouse(Page<Warehouse> warehouses){
      return Paging.builder().page(Long.valueOf(warehouses.getNumber()+1))
            .totalPage(Long.valueOf(warehouses.getTotalPages()))
            .itemPerPage(Long.valueOf(warehouses.getSize()))
            .totalItem(warehouses.getTotalElements()).build();
   }

   private List<GetWarehouseWebResponse> toGetWarehouseWebResponses(List<Warehouse> warehouseList){
      return warehouseList.stream().map(this::toGetWarehouseWebResponse).collect(Collectors.toList());
   }

   private GetWarehouseWebResponse toGetWarehouseWebResponse(Warehouse warehouse){
      return GetWarehouseWebResponse.builder()
            .code(warehouse.getWarehouseCode())
            .name(warehouse.getWarehouseName()).build();
   }
}
