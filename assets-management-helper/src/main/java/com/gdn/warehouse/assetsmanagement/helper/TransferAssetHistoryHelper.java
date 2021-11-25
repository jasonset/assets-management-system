package com.gdn.warehouse.assetsmanagement.helper;

import com.gdn.warehouse.assetsmanagement.helper.model.TransferAssetHistoryHelperRequest;
import reactor.core.publisher.Mono;

public interface TransferAssetHistoryHelper {
   Mono<Boolean> createTransferAssetHistory(TransferAssetHistoryHelperRequest request);
}
