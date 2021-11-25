package com.gdn.warehouse.assetsmanagement.command.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApproveMaintenanceRequestCommandRequest {
   private String maintenanceNumber;
   private Boolean approve;
   private String username;
}
