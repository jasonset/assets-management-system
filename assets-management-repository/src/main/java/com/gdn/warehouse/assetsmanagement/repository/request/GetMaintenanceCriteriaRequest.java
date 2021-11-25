package com.gdn.warehouse.assetsmanagement.repository.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMaintenanceCriteriaRequest {
   private String maintenanceNumberFilter;
   private String assetNumberFilter;
   private String requesterFilter;
   private String itemCodeFilter;
   private String locationFilter;
   private String statusFilter;
   private Date tanggalLaporanStartFilter;
   private Date tanggalLaporanEndFilter;
}
