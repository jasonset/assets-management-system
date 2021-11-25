package com.gdn.warehouse.assetsmanagement.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveredTransferAssetWebRequest {
   private String transferAssetNumber;
   private Long arrivalDate;
}
