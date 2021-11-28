package com.gdn.warehouse.assetsmanagement.command.impl;

import com.blibli.oss.backend.common.model.response.Paging;
import com.gdn.warehouse.assetsmanagement.command.GetAllItemCommand;
import com.gdn.warehouse.assetsmanagement.command.model.GetAllItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.repository.custom.ItemCustomRepository;
import com.gdn.warehouse.assetsmanagement.web.model.response.GetItemWebResponse;
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

@Slf4j
@Service
public class GetAllItemCommandImpl implements GetAllItemCommand {
   @Autowired
   private ItemCustomRepository itemCustomRepository;

   @Override
   public Mono<Pair<List<GetItemWebResponse>, Paging>> execute(GetAllItemCommandRequest request) {
      return mono(()-> constructCriteria(request))
            .flatMap(criteria -> itemCustomRepository.findByCriteria(criteria,request.getLimit(),request.getPage(),
                  constructSort(request)))
            .map(items -> Pair.of(toGetItemWebResponses(items.getContent()),getPagingForItem(items)));
   }

   private Criteria constructCriteria(GetAllItemCommandRequest request){
      Criteria criteria = new Criteria();
      if(StringUtils.isNotEmpty(request.getCodeFilter())){
         criteria = criteria.and("itemCode").regex(request.getCodeFilter());
      }

      if(StringUtils.isNotEmpty(request.getNameFilter())){
         criteria = criteria.and("itemName").regex(request.getNameFilter(),"i");
      }

      return criteria;
   }

   private Sort constructSort(GetAllItemCommandRequest request){
      return Sort.by(Sort.Direction.fromString(request.getSortOrder()),request.getSortBy());
   }

   private List<GetItemWebResponse> toGetItemWebResponses(List<Item> itemList){
      return itemList.stream().map(this::toGetItemWebResponse).collect(Collectors.toList());
   }

   private GetItemWebResponse toGetItemWebResponse(Item item){
      return GetItemWebResponse.builder()
            .code(item.getItemCode())
            .name(item.getItemName())
            .category(item.getCategory().name())
            .build();
   }

   private Paging getPagingForItem(Page<Item> items){
      return Paging.builder().page(Long.valueOf(items.getNumber()+1))
            .totalPage(Long.valueOf(items.getTotalPages()))
            .itemPerPage(Long.valueOf(items.getSize()))
            .totalItem(items.getTotalElements()).build();
   }
}
