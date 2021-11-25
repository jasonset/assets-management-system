package com.gdn.warehouse.assetsmanagement.repository.custom;

import com.gdn.warehouse.assetsmanagement.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import reactor.core.publisher.Mono;

public interface ItemCustomRepository {
   Mono<Page<Item>> findByCriteria(Criteria criteria, Integer limit, Integer page, Sort sort);
}
