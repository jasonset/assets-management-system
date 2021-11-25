package com.gdn.warehouse.assetsmanagement.helper;

import com.gdn.warehouse.assetsmanagement.enums.DocumentType;
import com.gdn.warehouse.assetsmanagement.helper.model.GenerateAssetNumberRequest;
import reactor.core.publisher.Mono;

public interface GenerateSequenceHelper {
   Mono<String> generateDocumentNumber(DocumentType documentType);
   Mono<String> generateDocumentNumberForAsset(DocumentType documentType, GenerateAssetNumberRequest request);
}
