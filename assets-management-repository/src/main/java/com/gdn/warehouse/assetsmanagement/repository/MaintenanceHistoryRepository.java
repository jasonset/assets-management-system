package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.MaintenanceHistory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MaintenanceHistoryRepository extends ReactiveMongoRepository<MaintenanceHistory, ObjectId> {
   Flux<MaintenanceHistory> findByMaintenanceNumber(String maintenanceNumber);
   Flux<MaintenanceHistory> findByMaintenanceNumberOrderByUpdatedDateAsc(String maintenanceNumber);
}
