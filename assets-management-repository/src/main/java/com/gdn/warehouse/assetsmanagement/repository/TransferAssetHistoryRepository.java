package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.TransferAssetHistory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransferAssetHistoryRepository extends ReactiveMongoRepository<TransferAssetHistory, ObjectId> {
   Flux<TransferAssetHistory> findByTransferAssetNumberOrderByUpdatedDateAsc(String transferAssetNumber);
}
