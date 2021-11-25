package com.gdn.warehouse.assetsmanagement.helper.model;

import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.Organisation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateAssetNumberRequest {
   private String organisation;
   private String purchase;
   private String category;
}
