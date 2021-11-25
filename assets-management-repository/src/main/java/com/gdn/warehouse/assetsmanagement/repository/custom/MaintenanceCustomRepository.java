package com.gdn.warehouse.assetsmanagement.repository.custom;

import com.gdn.warehouse.assetsmanagement.entity.Maintenance;
import com.gdn.warehouse.assetsmanagement.repository.request.GetMaintenanceCriteriaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

public interface MaintenanceCustomRepository {
   Mono<Page<Maintenance>> findByCriteria(GetMaintenanceCriteriaRequest request,Integer limit, Integer page, Sort sort);
}
