package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface MaintenanceRepository extends ReactiveMongoRepository<Maintenance, ObjectId> {
   Mono<Maintenance> findByMaintenanceNumber(String maintenanceNumber);
   Mono<Maintenance> findByAssetNumbersInAndStatusIn(List<String> assetNumbers, List<MaintenanceStatus> statuses);
}
