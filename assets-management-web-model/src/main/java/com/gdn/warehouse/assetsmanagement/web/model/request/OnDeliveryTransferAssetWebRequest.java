package com.gdn.warehouse.assetsmanagement.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnDeliveryTransferAssetWebRequest {
   private String transferAssetNumber;
   private Long deliveryDate;
   private Integer deliveryFee;
}
