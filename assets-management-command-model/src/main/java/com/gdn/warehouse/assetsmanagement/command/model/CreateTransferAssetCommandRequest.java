package com.gdn.warehouse.assetsmanagement.command.model;

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
public class CreateTransferAssetCommandRequest {
   private List<String> assetNumbers;
   private String destination;
   private String notes;
   private String username;
   private TransferAssetType transferAssetType;
}
