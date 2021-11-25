package com.gdn.warehouse.assetsmanagement.repository.custom;

import com.gdn.warehouse.assetsmanagement.entity.MaintenanceReminder;
import com.gdn.warehouse.assetsmanagement.repository.request.GetMaintenanceReminderCriteriaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;

public interface MaintenanceReminderCustomRepository {
   Mono<Page<MaintenanceReminder>> findByCriteria(GetMaintenanceReminderCriteriaRequest request, Integer limit,
                                                  Integer page, Sort sort);
}
