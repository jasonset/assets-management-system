package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.Item;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface ItemRepository extends ReactiveMongoRepository<Item, ObjectId> {
   Mono<Item> findByItemCode(String itemCode);
   Mono<Item> findByItemName(String itemName);
   Flux<Item> findByItemCodeIn(List<String> itemCodes);
}
