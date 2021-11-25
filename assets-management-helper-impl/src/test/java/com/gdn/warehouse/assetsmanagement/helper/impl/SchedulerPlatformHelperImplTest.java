package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.blibli.oss.backend.scheduler.platform.model.CancelDelayedJobRequest;
import com.blibli.oss.backend.scheduler.platform.model.DelayedJobRequest;
import com.blibli.oss.backend.scheduler.platform.repository.SchedulerPlatformRepository;
import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.properties.ScheduleProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SchedulerPlatformHelperImplTest {
   @InjectMocks
   private SchedulerPlatformHelperImpl helper;

   @Mock
   private SchedulerPlatformRepository schedulerPlatformRepository;

   @Mock
   private ScheduleProperties scheduleProperties;

   private Schedule schedule;

   @Before
   public void setUp(){
      MockitoAnnotations.initMocks(this);
      schedule = Schedule.builder()
            .nextScheduledTime(new Date()).build();
   }

   @Test
   public void sendToSchedulerPlatform(){
      when(schedulerPlatformRepository.send(any(DelayedJobRequest.class))).thenReturn(Mono.empty());
      helper.sendToSchedulerPlatform(schedule);
      verify(schedulerPlatformRepository).send(any(DelayedJobRequest.class));
   }

   @Test
   public void sendCancellationToSchedulerPlatform(){
      when(schedulerPlatformRepository.send(any(CancelDelayedJobRequest.class))).thenReturn(Mono.empty());
      helper.sendCancellationToSchedulerPlatform(schedule);
      verify(schedulerPlatformRepository).send(any(CancelDelayedJobRequest.class));
   }
}