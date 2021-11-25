package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.UpdateItemCommand;
import com.gdn.warehouse.assetsmanagement.command.model.UpdateItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.repository.AssetRepository;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@Service
public class UpdateItemCommandImpl implements UpdateItemCommand {
   @Autowired
   private ItemRepository itemRepository;

   @Autowired
   private AssetRepository assetRepository;

   @Override
   public Mono<String> execute(UpdateItemCommandRequest request) {
      return itemRepository.findByItemCode(request.getItemCode())
            .switchIfEmpty(Mono.defer(()->Mono.error(new CommandErrorException("Item doesn't exist!",HttpStatus.BAD_REQUEST))))
            .flatMap(item -> validateRequest(request.getCategory(),item)
                  .flatMap(result -> {
                     item.setItemName(request.getItemName());
                     item.setCategory(AssetCategory.valueOf(request.getCategory()));
                     item.setLastModifiedBy(request.getUsername());
                     item.setLastModifiedDate(new Date());
                     return itemRepository.save(item);
                  })).map(Item::getItemCode);
   }

   private Mono<Boolean> validateRequest(String category, Item item){
      if (category.equals(item.getCategory().name())){
         return Mono.defer(()->Mono.just(Boolean.TRUE));
      }else {
         return assetRepository.existsByItemCode(item.getItemCode())
               .flatMap(result -> {
                  if(BooleanUtils.isTrue(result)){
                     return Mono.defer(()->Mono.error(new CommandErrorException("Currently there's asset with this item, unable to change item CATEGORY, please change it first!", HttpStatus.BAD_REQUEST)));
                  }else {
                     return Mono.defer(()->Mono.just(Boolean.TRUE));
                  }
               });
      }
   }
}
