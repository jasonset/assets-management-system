package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.SystemParam;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SystemParamRepository extends ReactiveMongoRepository<SystemParam, ObjectId> {
   Mono<SystemParam> findByKey(String key);
}
