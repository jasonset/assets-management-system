package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.repository.custom.MaintenanceReminderCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetMaintenanceReminderCriteriaRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MaintenanceReminderCustomRepositoryImpl extends GenericCustomRepository implements MaintenanceReminderCustomRepository {
   @Override
   public Mono<Page<MaintenanceReminder>> findByCriteria(GetMaintenanceReminderCriteriaRequest request, Integer limit, Integer page, Sort sort) {
      return findByCriteria(MaintenanceReminder.class,constructCriteria(request),limit,page,sort);
   }

   private Criteria constructCriteria(GetMaintenanceReminderCriteriaRequest request){
      Criteria criteria = new Criteria();
      if(StringUtils.isNotEmpty(request.getMaintenanceReminderNumberFilter())){
         criteria = criteria.and("maintenanceReminderNumber").regex(request.getMaintenanceReminderNumberFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getAssetNumberFilter())){
         criteria = criteria.and("assetNumbers").regex(request.getAssetNumberFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getItemCodeFilter())){
         criteria = criteria.and("itemCode").regex(request.getItemCodeFilter(),"i");
      }
      if(StringUtils.isNotEmpty(request.getEmailFilter())){
         criteria = criteria.and("emailList").regex(request.getEmailFilter(),"i");
      }
      if(ObjectUtils.isNotEmpty(request.getIntervalFilter()) &&0!=request.getIntervalFilter()){
         criteria = criteria.and("interval").is(request.getIntervalFilter());
      }
      if(ObjectUtils.isNotEmpty(request.getScheduledDateFilter())){
         criteria = criteria.and("scheduledDate").gte(request.getScheduledDateFilter())
               .lte(request.getScheduledDateFilter());
      }
      if(ObjectUtils.isNotEmpty(request.getPreviousExecutionTimeFilter())){
         criteria = criteria.and("previousExecutionTime").gte(request.getPreviousExecutionTimeFilter())
               .lte(request.getPreviousExecutionTimeFilter());
      }
      criteria = criteria.and("deleted").is(Boolean.FALSE);

      return criteria;
   }
}
