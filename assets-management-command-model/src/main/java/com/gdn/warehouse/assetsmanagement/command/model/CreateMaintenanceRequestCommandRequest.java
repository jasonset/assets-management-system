package com.gdn.warehouse.assetsmanagement.command.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMaintenanceRequestCommandRequest {
   private String requester;
   private String requesterEmail;
   private List<String> assetNumbers;
   private Date tanggalKerusakan;
   private String deskripsiKerusakan;
   private Date tanggalLaporan;
   private String username;
}
