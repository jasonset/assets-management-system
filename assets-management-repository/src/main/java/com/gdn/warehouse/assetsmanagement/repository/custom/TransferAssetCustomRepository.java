package com.gdn.warehouse.assetsmanagement.repository.custom;

import com.gdn.warehouse.assetsmanagement.entity.TransferAsset;
import com.gdn.warehouse.assetsmanagement.repository.request.GetTransferAssetCriteriaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

public interface TransferAssetCustomRepository {
   Mono<Page<TransferAsset>> findByCriteria(GetTransferAssetCriteriaRequest request, Integer limit, Integer page, Sort sort);
}
