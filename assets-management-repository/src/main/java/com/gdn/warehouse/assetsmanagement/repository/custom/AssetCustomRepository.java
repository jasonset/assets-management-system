package com.gdn.warehouse.assetsmanagement.repository.custom;

import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.repository.request.GetAssetCriteriaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

public interface AssetCustomRepository {
   Mono<Page<Asset>> findByCriteria(GetAssetCriteriaRequest request, Integer limit, Integer page, Sort sort);
}
