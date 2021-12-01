package com.gdn.warehouse.assetsmanagement.repository.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMaintenanceReminderCriteriaRequest {
   private String maintenanceReminderNumberFilter;
   private String assetNumberFilter;
   private String itemCodeFilter;
   private Date scheduledDateFilter;
   private Date previousExecutionTimeFilter;
   private Integer intervalFilter;
   private String emailFilter;
}
