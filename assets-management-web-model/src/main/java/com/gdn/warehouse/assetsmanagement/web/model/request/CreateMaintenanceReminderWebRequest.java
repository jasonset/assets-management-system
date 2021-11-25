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
public class CreateMaintenanceReminderWebRequest {
   private List<String> assetNumbers;
   private Long scheduledDate;
   private Integer schedule;
   private List<String> emailList;
}
