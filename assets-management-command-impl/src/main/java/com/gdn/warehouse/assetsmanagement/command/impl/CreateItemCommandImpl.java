package com.gdn.warehouse.assetsmanagement.command.impl;

import com.gdn.warehouse.assetsmanagement.command.CreateItemCommand;
import com.gdn.warehouse.assetsmanagement.command.model.CreateItemCommandRequest;
import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.helper.GenerateSequenceHelper;
import com.gdn.warehouse.assetsmanagement.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@Service
public class CreateItemCommandImpl implements CreateItemCommand {
   @Autowired
   private ItemRepository itemRepository;

   @Autowired
   private GenerateSequenceHelper generateSequenceHelper;

   @Override
   public Mono<String> execute(CreateItemCommandRequest request) {
      return validateRequest(request)
            .flatMap(result -> generateSequenceHelper.generateDocumentNumber(DocumentType.ITEM)
                  .flatMap(itemCode -> itemRepository.save(Item.builder()
                        .itemCode(itemCode)
                        .itemName(request.getItemName())
                        .category(AssetCategory.valueOf(request.getCategory()))
                        .createdBy(request.getUsername())
                        .createdDate(new Date())
                        .lastModifiedBy(request.getUsername())
                        .lastModifiedDate(new Date()).build()))
                  .map(Item::getItemCode));
   }

   private Mono<String> validateRequest(CreateItemCommandRequest request){
      String itemName = request.getItemName().trim();
      return itemRepository.findByItemName(itemName)
            .flatMap(item -> Mono.defer(()->Mono.<String>error(new CommandErrorException("Item Name already exist with code "
                  +item.getItemCode(), HttpStatus.BAD_REQUEST))))
            .switchIfEmpty(Mono.defer(()->Mono.just(request.getItemName())));
   }

}
