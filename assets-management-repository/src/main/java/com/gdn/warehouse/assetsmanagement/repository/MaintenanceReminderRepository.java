package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MaintenanceReminderRepository extends ReactiveMongoRepository<MaintenanceReminder, ObjectId> {
   Mono<MaintenanceReminder> findByMaintenanceReminderNumber(String maintenanceReminderNumber);
}
