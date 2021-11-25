package com.gdn.warehouse.assetsmanagement.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTransferAssetWebRequest {
   private String transferAssetNumber;
   private String assetNumber;
   private String origin;
   private String destination;
   private String itemCode;
   private String status;
   private String transferAssetType;
   private String referenceNumber;
}
