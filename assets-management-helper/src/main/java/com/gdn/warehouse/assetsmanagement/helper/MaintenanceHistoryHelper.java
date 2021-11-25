package com.gdn.warehouse.assetsmanagement.helper;

import com.gdn.warehouse.assetsmanagement.helper.model.MaintenanceHistoryHelperRequest;
import reactor.core.publisher.Mono;

public interface MaintenanceHistoryHelper {
   Mono<Boolean> createMaintenanceHistory(MaintenanceHistoryHelperRequest request);
}
