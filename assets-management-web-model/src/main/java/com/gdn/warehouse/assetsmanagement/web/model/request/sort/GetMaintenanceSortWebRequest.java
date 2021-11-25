package com.gdn.warehouse.assetsmanagement.web.model.request.sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMaintenanceSortWebRequest {
   private String maintenanceNumber;
   private String tanggalLaporan;
}
