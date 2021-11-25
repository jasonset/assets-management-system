package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.Item;
import com.gdn.warehouse.assetsmanagement.repository.custom.ItemCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ItemCustomRepositoryImpl extends GenericCustomRepository implements ItemCustomRepository {

   @Override
   public Mono<Page<Item>> findByCriteria(Criteria criteria, Integer limit, Integer page, Sort sort) {
      return findByCriteria(Item.class,criteria,limit,page,sort);
   }
}
