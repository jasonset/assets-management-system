package com.gdn.warehouse.assetsmanagement.helper;

import com.gdn.warehouse.assetsmanagement.entity.Asset;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AssetValidatorHelper {
   Mono<List<Asset>> validateAssetFromRequest(List<String> assetNumbers);

   Mono<List<Asset>> validateAssetForMaintenanceReminder(List<String> assetNumbers);
}
