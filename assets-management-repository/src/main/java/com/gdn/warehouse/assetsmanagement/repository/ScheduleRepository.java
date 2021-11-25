package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ScheduleRepository extends ReactiveMongoRepository<Schedule, ObjectId> {
   Mono<Schedule> findByIdentifier(String identifier);

   Flux<Schedule> findByTopicNameAndEnabled(String topic, Boolean enabled);
}
