package com.gdn.warehouse.assetsmanagement.repository.custom;

import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import reactor.core.publisher.Mono;

public interface WarehouseCustomRepository {
   Mono<Page<Warehouse>> findByCriteria(Criteria criteria, Integer limit, Integer page, Sort sort);
}
