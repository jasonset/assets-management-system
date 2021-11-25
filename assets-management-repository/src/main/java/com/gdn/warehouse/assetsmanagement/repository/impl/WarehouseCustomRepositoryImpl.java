package com.gdn.warehouse.assetsmanagement.repository.impl;

import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import com.gdn.warehouse.assetsmanagement.repository.custom.WarehouseCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WarehouseCustomRepositoryImpl extends GenericCustomRepository implements WarehouseCustomRepository {

   @Override
   public Mono<Page<Warehouse>> findByCriteria(Criteria criteria, Integer limit, Integer page, Sort sort) {
      return findByCriteria(Warehouse.class,criteria,limit,page,sort);
   }
}
