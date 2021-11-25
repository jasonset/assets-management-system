package com.gdn.warehouse.assetsmanagement.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMaintenanceWebRequest {
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
}
