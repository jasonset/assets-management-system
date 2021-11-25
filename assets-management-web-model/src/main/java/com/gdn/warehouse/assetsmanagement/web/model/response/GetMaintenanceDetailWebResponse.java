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
public class GetMaintenanceDetailWebResponse {
   private String maintenanceNumber;
   private String requester;
   private List<String> assetNumbers;
   private String status;
   private String itemName;
   private String location;
//   private String poNumber;
//   private Date poIssuedDate;
   private Date tanggalKerusakan;
   private Date tanggalLaporan;
   private String deskripsiKerusakan;
   private String warehouseManagerEmail;
   private Integer maintenanceFee;
   private Date quoSubmit;
   private Date poSubmit;
   private Date poApproved;
   private Date tanggalService;
   private Date tanggalNormal;
   private Boolean reject;
   private String alasanReject;
   private String notes;
}
