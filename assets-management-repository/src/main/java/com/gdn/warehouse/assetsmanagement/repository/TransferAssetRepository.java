package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

@Repository
public interface TransferAssetRepository extends ReactiveMongoRepository<TransferAsset, ObjectId> {

   Flux<TransferAsset> findByAssetNumbers(List<String> assetNumbers);

   Mono<Boolean> existsByAssetNumbers(List<String> assetNumbers);

   Mono<TransferAsset> findByTransferAssetNumber(String transferAssetNumber);
}
