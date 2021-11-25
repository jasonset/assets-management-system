package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.DocumentSequence;
import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DocumentSequenceRepository extends ReactiveMongoRepository<DocumentSequence, ObjectId> {
   Mono<DocumentSequence> findByDocumentType(DocumentType documentType);
   Mono<DocumentSequence> findByDocumentTypeAndCategory(DocumentType documentType, AssetCategory category);
}
