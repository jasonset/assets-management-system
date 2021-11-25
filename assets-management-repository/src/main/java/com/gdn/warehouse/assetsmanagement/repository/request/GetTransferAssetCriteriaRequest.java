package com.gdn.warehouse.assetsmanagement.repository.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTransferAssetCriteriaRequest {
   private String transferAssetNumberFilter;
   private String assetNumberFilter;
   private String originFilter;
   private String destinationFilter;
   private String itemCodeFilter;
   private String statusFilter;
   private String transferAssetTypeFilter;
   private String referenceNumberFilter;
}
