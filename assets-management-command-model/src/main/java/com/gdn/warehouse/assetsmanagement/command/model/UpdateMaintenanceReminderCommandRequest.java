package com.gdn.warehouse.assetsmanagement.command.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMaintenanceReminderCommandRequest {
   private String maintenanceReminderNumber;
   private Boolean enabled;
   private Integer interval;
   private List<String> assetNumbers;
   private List<String> emailList;
   private Long scheduledDate;
   private String username;
}
