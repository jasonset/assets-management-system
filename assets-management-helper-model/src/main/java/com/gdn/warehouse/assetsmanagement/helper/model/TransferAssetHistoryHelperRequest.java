package com.gdn.warehouse.assetsmanagement.helper.model;

import com.gdn.warehouse.assetsmanagement.enums.TransferAssetStatus;
import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferAssetHistoryHelperRequest {
   private String transferAssetNumber;
   private TransferAssetStatus transferAssetStatus;
   private TransferAssetType transferAssetType;
   private Date updatedDate;
   private String updatedBy;
}
