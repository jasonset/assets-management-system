package com.gdn.warehouse.assetsmanagement.helper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleHelperRequest {
   private String topic;
   private String identifier;
   private String payload;
   private Integer interval;
   private TimeUnit timeUnit;
   private Date nextSchedule;
   private Date lastExecutionTime;
   private String username;
}
