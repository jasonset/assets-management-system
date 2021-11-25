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
public class GetMaintenanceWebResponse {
   private String maintenanceNumber;
   private List<String> assetNumbers;
   private String requester;
   private String itemName;
   private String location;
   private Date tanggalLaporan;
   private String status;
}
