package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.request.SortBy;
import com.blibli.oss.backend.common.model.response.Paging;
import com.gdn.warehouse.assetsmanagement.command.GetAllItemWithFilterCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllItemWithFilterCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.properties.StringConstants;
import com.gdn.warehouse.assetsmanagement.repository.custom.ItemCustomRepository;
import com.gdn.warehouse.assetsmanagement.web.model.response.ItemResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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

@Slf4j
@Service
public class GetAllItemWithFilterCommandImpl implements GetAllItemWithFilterCommand {
   @Autowired
   private ItemCustomRepository itemCustomRepository;

   @Override
   public Mono<Pair<List<ItemResponse>, Paging>> execute(GetAllItemWithFilterCommandRequest request) {
      return mono(()-> constructCriteria(request))
            .flatMap(criteria -> itemCustomRepository.findByCriteria(criteria,request.getLimit(),request.getPage(),
                  constructSort(request.getSortBy())))
            .map(items -> Pair.of(toGetItemResponses(items.getContent()),getPagingForItem(items)));
   }

   private Criteria constructCriteria(GetAllItemWithFilterCommandRequest request){
      Criteria criteria = new Criteria();
      if(StringUtils.isNotEmpty(request.getCodeFilter())){
         criteria = criteria.and("itemCode").regex(request.getCodeFilter(),"i");
      }

      if(StringUtils.isNotEmpty(request.getNameFilter())){
         criteria = criteria.and("itemName").regex(request.getNameFilter(),"i");
      }

      if(StringUtils.isNotEmpty(request.getCategoryFilter())){
         criteria = criteria.and("category").regex(request.getCategoryFilter(),"i");
      }

      return criteria;
   }

   private Sort constructSort(List<SortBy> sortBy){
      return CollectionUtils.isEmpty(sortBy)?
            Sort.by(Sort.Direction.fromString(StringConstants.DEFAULT_SORT_DIRECTION),"itemCode"):
            Sort.by(sortBy.stream()
                  .map(sort -> new Sort.Order(Sort.Direction.fromString(sort.getDirection().name()), sort.getPropertyName()))
                  .collect(Collectors.toList()));
   }

   private List<ItemResponse> toGetItemResponses(List<Item> itemList){
      return itemList.stream().map(item ->
            ItemResponse.builder().code(item.getItemCode()).name(item.getItemName()).category(item.getCategory().name()).build()
      ).collect(Collectors.toList());
   }

   private Paging getPagingForItem(Page<Item> items){
      return Paging.builder().page(Long.valueOf(items.getNumber()+1))
            .totalPage(Long.valueOf(items.getTotalPages()))
            .itemPerPage(Long.valueOf(items.getSize()))
            .totalItem(items.getTotalElements()).build();
   }
}
