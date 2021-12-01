package com.gdn.warehouse.assetsmanagement.web.model.request.sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMaintenanceReminderSortWebRequest {
   private String maintenanceReminderNumber;
   private String scheduledDate;
   private String previousExecutionTime;
}
