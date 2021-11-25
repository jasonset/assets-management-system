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

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Document(collection = TransferAssetHistory.TRANSFER_ASSET_HISTORY)
public class TransferAssetHistory {
   public static final String TRANSFER_ASSET_HISTORY = "transfer_asset_histories";

   private String transferAssetNumber;
   private TransferAssetType transferAssetType;
   private TransferAssetStatus transferAssetStatus;
   private Date updatedDate;
   private String updatedBy;
}
