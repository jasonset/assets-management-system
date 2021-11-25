package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.helper.ScheduleHelper;
import com.gdn.warehouse.assetsmanagement.helper.model.CreateScheduleHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class ScheduleHelperImpl implements ScheduleHelper {

   @Autowired
   private ScheduleRepository scheduleRepository;

   @Override
   public Mono<Schedule> saveSchedule(CreateScheduleHelperRequest request) {
      return scheduleRepository.findByIdentifier(request.getIdentifier())
            .map(schedule-> updateSchedule(request, schedule))
            .switchIfEmpty(Mono.defer(()-> Mono.fromSupplier(()->constructSchedule(request))))
            .flatMap(scheduleRepository::save);
   }

   private Schedule constructSchedule(CreateScheduleHelperRequest request){
      return Schedule.builder()
            .identifier(request.getIdentifier())
            .topicName(request.getTopic())
            .interval(request.getInterval())
            .nextScheduledTime(request.getNextSchedule())
            .payload(request.getPayload())
            .enabled(true)
            .createdBy(request.getUsername())
            .createdDate(new Date())
            .lastModifiedBy(request.getUsername())
            .lastModifiedDate(new Date())
            .build();
   }

   private Schedule updateSchedule(CreateScheduleHelperRequest request, Schedule schedule){
      schedule.setNextScheduledTime(request.getNextSchedule());
      schedule.setPayload(request.getPayload());
      schedule.setTimeUnit(request.getTimeUnit());
      schedule.setInterval(request.getInterval());
      schedule.setEnabled(true);
      schedule.setLastExecutionTime(Objects.isNull(request.getLastExecutionTime())? schedule.getLastExecutionTime(): request.getLastExecutionTime());
      schedule.setLastModifiedBy("SYSTEM");
      schedule.setLastModifiedDate(new Date());
      return schedule;
   }

   @Override
   public Mono<Schedule> cancelSchedule(String identifier, Date previousExecutionTime) {
      return scheduleRepository.findByIdentifier(identifier)
            .map(schedule -> {
               schedule.setEnabled(false);
               schedule.setLastExecutionTime(Objects.isNull(previousExecutionTime)?
                     schedule.getLastExecutionTime() : previousExecutionTime);
               return schedule;
            })
            .flatMap(scheduleRepository::save);
   }
}
