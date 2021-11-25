package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.entity.Schedule;
import com.gdn.warehouse.assetsmanagement.helper.model.CreateScheduleHelperRequest;
import com.gdn.warehouse.assetsmanagement.repository.ScheduleRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScheduleHelperImplTest {
   private static final String IDENTIFIER = "identifier";
   @InjectMocks
   private ScheduleHelperImpl helper;

   @Mock
   private ScheduleRepository scheduleRepository;

   private CreateScheduleHelperRequest helperRequest;
   private Schedule schedule;

   @Before
   public void setUp(){
      MockitoAnnotations.initMocks(this);

      helperRequest = CreateScheduleHelperRequest.builder()
            .identifier(IDENTIFIER).build();
      schedule = Schedule.builder().lastExecutionTime(new Date()).build();
   }

   @Test
   public void saveSchedule(){
      when(scheduleRepository.findByIdentifier(any(String.class))).thenReturn(Mono.empty());
      when(scheduleRepository.save(any(Schedule.class))).thenReturn(Mono.just(new Schedule()));
      Schedule schedule = helper.saveSchedule(helperRequest).block();
      Assert.assertNotNull(schedule);
      verify(scheduleRepository).findByIdentifier(IDENTIFIER);
      verify(scheduleRepository).save(any(Schedule.class));
   }

   @Test
   public void saveSchedule_exists(){
      when(scheduleRepository.findByIdentifier(IDENTIFIER)).thenReturn(Mono.just(new Schedule()));
      when(scheduleRepository.save(any(Schedule.class))).thenReturn(Mono.just(new Schedule()));
      Schedule schedule = helper.saveSchedule(helperRequest).block();
      Assert.assertNotNull(schedule);
      verify(scheduleRepository).findByIdentifier(IDENTIFIER);
      verify(scheduleRepository).save(any(Schedule.class));
   }

   @Test
   public void cancelSchedule(){
      when(scheduleRepository.findByIdentifier(IDENTIFIER)).thenReturn(Mono.just(schedule));
      when(scheduleRepository.save(any(Schedule.class))).thenReturn(Mono.just(schedule));
      helper.cancelSchedule(IDENTIFIER, new Date()).block();
      verify(scheduleRepository).findByIdentifier(IDENTIFIER);
      verify(scheduleRepository).save(any(Schedule.class));
   }

   @Test
   public void cancelSchedule_previousExecutionTime_null(){
      when(scheduleRepository.findByIdentifier(IDENTIFIER)).thenReturn(Mono.just(schedule));
      when(scheduleRepository.save(any(Schedule.class))).thenReturn(Mono.just(schedule));
      helper.cancelSchedule(IDENTIFIER, null).block();
      verify(scheduleRepository).findByIdentifier(IDENTIFIER);
      verify(scheduleRepository).save(any(Schedule.class));
   }

}