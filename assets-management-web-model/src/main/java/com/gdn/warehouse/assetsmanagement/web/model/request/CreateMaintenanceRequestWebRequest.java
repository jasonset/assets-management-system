package com.gdn.warehouse.assetsmanagement.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMaintenanceRequestWebRequest {
   private String requester;
   private String requesterEmail;
   private List<String> assetNumbers;
   private Long tanggalKerusakan;
   private String deskripsiKerusakan;
}
