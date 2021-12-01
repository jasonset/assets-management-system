package com.gdn.warehouse.assetsmanagement.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMaintenanceReminderWebResponse {
   private String maintenanceReminderNumber;
   private List<String> assetNumbers;
   private String itemName;
   private Date scheduledDate;
   private Integer interval;
   private List<String> emailList;
   private Boolean enabled;
   private Date previousExecutionTime;
}
