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
public class GetTransferAssetDetailWebResponse {
   private String transferAssetNumber;
   private List<String> assetNumbers;
   private String itemName;
   private String origin;
   private String destination;
   private String status;
   private String notes;
   private Date arrivalDate;
   private Date deliveryDate;
   private String referenceNumber;
   private String transferAssetType;
   private Date duration;
}
