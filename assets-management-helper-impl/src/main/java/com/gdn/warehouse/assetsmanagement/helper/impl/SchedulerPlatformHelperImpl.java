package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.blibli.oss.backend.scheduler.platform.model.CancelDelayedJobRequest;
import com.blibli.oss.backend.scheduler.platform.model.DelayedJobRequest;
import com.blibli.oss.backend.scheduler.platform.repository.SchedulerPlatformRepository;
import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.helper.SchedulerPlatformHelper;
import com.gdn.warehouse.assetsmanagement.properties.ScheduleProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class SchedulerPlatformHelperImpl implements SchedulerPlatformHelper {

   @Autowired
   private SchedulerPlatformRepository schedulerPlatformRepository;

   @Autowired
   private ScheduleProperties scheduleProperties;

   @Override
   public void sendToSchedulerPlatform(Schedule schedule) {
      DelayedJobRequest delayedJobRequest = constructDelayedJobRequest(schedule);
      schedulerPlatformRepository.send(delayedJobRequest).subscribe();
   }

   private DelayedJobRequest constructDelayedJobRequest(Schedule schedule){
      return DelayedJobRequest.builder()
            .id(schedule.getIdentifier())
            .name(schedule.getIdentifier())
            .topic(schedule.getTopicName())
            .payload(schedule.getPayload())
            .group(scheduleProperties.getGroupName())
            .notifyTimes(Arrays.asList(schedule.getNextScheduledTime().getTime())).build();
   }

   @Override
   public void sendCancellationToSchedulerPlatform(Schedule schedule) {
      schedulerPlatformRepository.send(constructCancelDelayedJobRequest(schedule)).subscribe();
   }

   private CancelDelayedJobRequest constructCancelDelayedJobRequest(Schedule schedule){
      return CancelDelayedJobRequest.builder()
            .id(schedule.getIdentifier())
            .name(schedule.getIdentifier())
            .group(scheduleProperties.getGroupName())
            .build();
   }
}
