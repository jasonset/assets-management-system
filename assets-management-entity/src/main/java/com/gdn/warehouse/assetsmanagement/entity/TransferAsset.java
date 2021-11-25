package com.gdn.warehouse.assetsmanagement.entity;

import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Document(collection = TransferAsset.TRANSFER_ASSET_COLLECTIONS)
public class TransferAsset extends BaseEntity {

   public static final String TRANSFER_ASSET_COLLECTIONS = "transfer_assets";

   private String transferAssetNumber;
   private List<String> assetNumbers;
   private String referenceNumber;
   private String itemCode;
   private String origin;
   private String destination;
   private TransferAssetStatus status;
   private String notes;
   private Date deliveryDate;
   private Date arrivalDate;
   private String originWarehouseManagerEmail;
   private String destinationWarehouseManagerEmail;
   private TransferAssetType transferAssetType;
}
