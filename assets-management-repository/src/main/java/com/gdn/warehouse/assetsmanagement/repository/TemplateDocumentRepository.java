package com.gdn.warehouse.assetsmanagement.repository;

import com.gdn.warehouse.assetsmanagement.entity.TemplateDocument;
import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TemplateDocumentRepository extends ReactiveMongoRepository<TemplateDocument, ObjectId> {
   Mono<TemplateDocument> findByDocumentType(DocumentType documentType);
}
