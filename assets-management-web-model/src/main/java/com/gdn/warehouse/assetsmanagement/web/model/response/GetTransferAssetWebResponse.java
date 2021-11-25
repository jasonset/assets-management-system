package com.gdn.warehouse.assetsmanagement.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTransferAssetWebResponse {
   private String transferAssetNumber;
   private List<String> assetNumbers;
   private String origin;
   private String destination;
   private String itemName;
   private String status;
   private String transferAssetType;
}
