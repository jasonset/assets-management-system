package com.gdn.warehouse.assetsmanagement.command.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMaintenanceCommandRequest {
   private String maintenanceNumber;
   private Integer maintenanceFee;
   private Long tanggalKerusakan;
   private String deskripsiKerusakan;
   private Long quoSubmit;
   private Long poSubmit;
   private Long poApproved;
   private Long tanggalService;
   private Long tanggalNormal;
   private String notes;
   private String username;
}
