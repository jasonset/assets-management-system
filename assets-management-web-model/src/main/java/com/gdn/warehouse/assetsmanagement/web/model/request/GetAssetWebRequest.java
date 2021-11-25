package com.gdn.warehouse.assetsmanagement.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAssetWebRequest {
   private String assetNumber;
   private String organisation;
   private String vendor;
   private String itemCode;
   private String location;
   private String status;
   private String category;
}
