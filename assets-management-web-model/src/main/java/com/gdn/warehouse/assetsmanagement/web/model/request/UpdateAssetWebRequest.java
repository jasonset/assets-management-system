package com.gdn.warehouse.assetsmanagement.web.model.request;

import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.Organisation;
import com.gdn.warehouse.assetsmanagement.enums.Purchase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssetWebRequest {
   private String assetNumber;
   private Organisation organisation;
   private String itemCode;
   private String vendor;
   private String location;
   private String poNumber;
   private Long poIssuedDate;
   private Integer price;
   private AssetStatus status;
   private Long deliveryDate;
   private String notes;
   private String vehiclePlate;
   private String nomorRangka;
   private String nomorMesin;
   private Purchase purchase;
}
