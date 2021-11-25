package com.gdn.warehouse.assetsmanagement.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAssetDetailWebResponse {
   private String assetNumber;
   private String organisation;
   private String itemName;
   private String itemCode;
   private String vendor;
   private String location;
   private String poNumber;
   private Date poIssuedDate;
   private Integer price;
   private String status;
   private Date deliveryDate;
   private String notes;
   private String purchase;
   private String category;
   private String vehiclePlate;
   private String nomorRangka;
   private String nomorMesin;
   private String dipinjam;
}
