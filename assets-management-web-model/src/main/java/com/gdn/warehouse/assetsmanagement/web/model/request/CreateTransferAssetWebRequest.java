package com.gdn.warehouse.assetsmanagement.web.model.request;

import com.gdn.warehouse.assetsmanagement.enums.TransferAssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferAssetWebRequest {
   private List<String> assetNumbers;
   private String destination;
   private String notes;
   private TransferAssetType transferAssetType;
}
