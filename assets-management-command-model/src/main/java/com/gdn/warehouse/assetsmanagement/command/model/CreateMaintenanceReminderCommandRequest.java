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
public class CreateMaintenanceReminderCommandRequest {
   private List<String> assetNumbers;
   private Long scheduledDate;
   private Integer interval;
   private List<String> emailList;
   private String username;
}
