package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.Asset;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface AssetRepository extends ReactiveMongoRepository<Asset, ObjectId> {
   Mono<Asset> findByAssetNumber(String assetNumber);
   Flux<Asset> findByAssetNumberIn(List<String> assetNumber);
   Flux<Asset> findByItemCode(String itemCode);
   Mono<Boolean> existsByItemCode(String itemCode);
}
