package com.gdn.warehouse.assetsmanagement.entity;

import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import com.gdn.warehouse.assetsmanagement.enums.AssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.Organisation;
import com.gdn.warehouse.assetsmanagement.enums.Purchase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = Asset.ASSET_COLLECTIONS)
public class Asset extends BaseEntity{

   public static final String ASSET_COLLECTIONS = "assets";

   private String assetNumber;
   private Organisation organisation;
   private String itemCode;
   private String vendor;
   private String location;
   private String warehouseCode;
   private String poNumber;
   private Date poIssuedDate;
   private Integer price;
   private AssetStatus status;
   private Date deliveryDate;
   private String notes;
   private String vehiclePlate;
   private String nomorRangka;
   private String nomorMesin;
   private Purchase purchase;
   private AssetCategory category;
   private Boolean hasReminder;
   private Boolean dipinjam;
}
