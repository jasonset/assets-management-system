package com.gdn.warehouse.assetsmanagement.helper.model;

import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceHistoryHelperRequest {
   private String maintenanceNumber;
   private MaintenanceStatus maintenanceStatus;
   private Date updatedDate;
   private String updatedBy;
}
