package com.gdn.warehouse.assetsmanagement.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMaintenanceReminderWebRequest {
   private String maintenanceReminderNumber;
   private String assetNumber;
   private String itemCode;
   private Integer interval;
   private Long scheduledDate;
   private String email;
}
