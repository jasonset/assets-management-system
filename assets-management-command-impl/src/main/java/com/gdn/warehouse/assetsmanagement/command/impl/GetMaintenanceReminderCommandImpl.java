package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.response.Paging;
import com.gdn.warehouse.assetsmanagement.command.GetMaintenanceReminderCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetMaintenanceReminderCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import com.gdn.warehouse.assetsmanagement.repository.custom.MaintenanceReminderCustomRepository;
import com.gdn.warehouse.assetsmanagement.repository.request.GetMaintenanceReminderCriteriaRequest;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetMaintenanceReminderWebResponse;
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

@Slf4j
@Service
public class GetMaintenanceReminderCommandImpl implements GetMaintenanceReminderCommand {

   @Autowired
   private MaintenanceReminderCustomRepository maintenanceReminderCustomRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Override
   public Mono<Pair<List<GetMaintenanceReminderWebResponse>,Paging>> execute(GetMaintenanceReminderCommandRequest request) {
      return maintenanceReminderCustomRepository.findByCriteria(constructCriteriaRequest(request), request.getLimit(),
            request.getPage(), constructSort(request.getSortBy()))
            .flatMap(maintenanceReminders -> getAllItems(maintenanceReminders.getContent())
                  .map(itemMap -> Pair.of(toGetMaintenanceReminderWebResponse(maintenanceReminders.getContent(),itemMap),getPaginationForMaintenanceReminder(maintenanceReminders))));
   }

   private Sort constructSort(List<SortBy> sortBy){
      return CollectionUtils.isEmpty(sortBy)?
            Sort.by(Sort.Direction.fromString(StringConstants.DEFAULT_SORT_DIRECTION),"maintenanceNumber"):
            Sort.by(sortBy.stream()
                  .map(sort -> new Sort.Order(Sort.Direction.fromString(sort.getDirection().name()), sort.getPropertyName()))
                  .collect(Collectors.toList()));
   }

   private GetMaintenanceReminderCriteriaRequest constructCriteriaRequest(GetMaintenanceReminderCommandRequest request){
      return GetMaintenanceReminderCriteriaRequest.builder()
            .maintenanceReminderNumberFilter(request.getMaintenanceReminderNumberFilter())
            .assetNumberFilter(request.getAssetNumberFilter())
            .itemCodeFilter(request.getItemCodeFilter())
            .scheduledDateFilter(request.getScheduledDateFilter()==null?null:new Date(request.getScheduledDateFilter()))
            .previousExecutionTimeFilter(request.getPreviousExecutionTimeFilter()==null?null:new Date(request.getPreviousExecutionTimeFilter()))
            .intervalFilter(request.getIntervalFilter())
            .emailFilter(request.getEmailFilter()).build();
   }

   private Mono<Map<String,String>> getAllItems(List<MaintenanceReminder> maintenanceReminders){
      List<String> itemCodes = maintenanceReminders.stream().map(MaintenanceReminder::getItemCode).distinct().collect(Collectors.toList());
      return itemRepository.findByItemCodeIn(itemCodes).collectMap(Item::getItemCode, Item::getItemName);
   }

   private List<GetMaintenanceReminderWebResponse> toGetMaintenanceReminderWebResponse(List<MaintenanceReminder> maintenanceReminders, Map<String,String> itemMap){
      return maintenanceReminders.stream().map(maintenanceReminder ->
            GetMaintenanceReminderWebResponse.builder().maintenanceReminderNumber(maintenanceReminder.getMaintenanceReminderNumber())
                  .assetNumbers(maintenanceReminder.getAssetNumbers())
                  .itemName(itemMap.get(maintenanceReminder.getItemCode()))
                  .emailList(maintenanceReminder.getEmailList())
                  .scheduledDate(maintenanceReminder.getScheduledDate())
                  .previousExecutionTime(maintenanceReminder.getPreviousExecutionTime())
                  .interval(maintenanceReminder.getInterval())
                  .enabled(maintenanceReminder.getEnabled()).build()).collect(Collectors.toList());
   }

   private Paging getPaginationForMaintenanceReminder(Page<MaintenanceReminder> maintenanceReminders){
      return Paging.builder().itemPerPage((long)maintenanceReminders.getSize())
            .page((long)maintenanceReminders.getNumber()+1)
            .totalItem(maintenanceReminders.getTotalElements())
            .totalPage((long)maintenanceReminders.getTotalPages()).build();
   }
}
