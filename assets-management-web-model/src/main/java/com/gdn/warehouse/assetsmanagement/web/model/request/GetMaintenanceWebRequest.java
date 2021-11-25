package com.gdn.warehouse.assetsmanagement.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMaintenanceWebRequest {
   private String maintenanceNumber;
   private String assetNumber;
   private String requester;
   private String itemCode;
   private String location;
   private Long tanggalLaporanStart;
   private Long tanggalLaporanEnd;
   private String status;
}
