package com.gdn.warehouse.assetsmanagement.helper;

import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.helper.model.CreateScheduleHelperRequest;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface ScheduleHelper {
   Mono<Schedule> saveSchedule(CreateScheduleHelperRequest request);
   Mono<Schedule> cancelSchedule(String identifier, Date previousExecutionTime);
}
