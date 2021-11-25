package com.gdn.warehouse.assetsmanagement.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAssetWebResponse {
   private String assetNumber;
   private String organisation;
   private String vendor;
   private String itemName;
   private String location;
   private String status;
}
