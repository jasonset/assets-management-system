package com.gdn.warehouse.assetsmanagement.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMaintenanceReminderWebRequest {
   private String maintenanceReminderNumber;
   private Boolean enabled;
   private List<String> assetNumbers;
   private List<String> emailList;
   private Integer interval;
   private Long scheduledDate;
}
