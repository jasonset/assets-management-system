package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.custom.MaintenanceCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetMaintenanceCriteriaRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Component
public class MaintenanceCustomRepositoryImpl extends GenericCustomRepository implements MaintenanceCustomRepository {
   @Override
   public Mono<Page<Maintenance>> findByCriteria(GetMaintenanceCriteriaRequest request, Integer limit, Integer page, Sort sort) {
      return findByCriteria(Maintenance.class,constructCriteria(request),limit,page,sort);
   }

   private Criteria constructCriteria(GetMaintenanceCriteriaRequest request){
      Criteria criteria = new Criteria();
      if(StringUtils.isNotEmpty(request.getMaintenanceNumberFilter())){
         criteria = criteria.and("maintenanceNumber").regex(request.getMaintenanceNumberFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getAssetNumberFilter())){
         criteria = criteria.and("assetNumbers").regex(request.getAssetNumberFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getRequesterFilter())){
         criteria = criteria.and("requester").regex(request.getRequesterFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getLocationFilter())){
         criteria = criteria.and("location").regex(request.getLocationFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getItemCodeFilter())){
         criteria = criteria.and("itemCode").regex(request.getItemCodeFilter(),"i");
      }
      if(ObjectUtils.isNotEmpty(request.getTanggalLaporanStartFilter())&&
         ObjectUtils.isNotEmpty(request.getTanggalLaporanEndFilter())){
         criteria = criteria.and("tanggalLaporan").gte(request.getTanggalLaporanStartFilter())
               .lte(request.getTanggalLaporanEndFilter());
      }
      if(StringConstants.ONGOING.equals(request.getStatusFilter())){
         criteria = criteria.and("status").in(Arrays.asList("REQUEST_RECEIVED","SCHEDULED","ON_MAINTENANCE"));
      } else if (StringConstants.HISTORY.equals(request.getStatusFilter())){
         criteria = criteria.and("status").in(Arrays.asList("REQUEST_DECLINED","DONE","REJECTED"));
      }else if(StringUtils.isNotEmpty(request.getStatusFilter())){
         criteria = criteria.and("status").regex(request.getStatusFilter());
      }
      return criteria;
   }
}
