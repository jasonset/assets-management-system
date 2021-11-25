package com.gdn.warehouse.assetsmanagement.repository.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAssetCriteriaRequest {
   private String assetNumberFilter;
   private String organisationFilter;
   private String vendorFilter;
   private String itemCodeFilter;
   private String locationFilter;
   private String statusFilter;
   private String categoryFilter;
}
