package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.Warehouse;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface WarehouseRepository extends ReactiveMongoRepository<Warehouse, ObjectId> {
   Mono<Warehouse> findByWarehouseName(String warehouseName);
}
