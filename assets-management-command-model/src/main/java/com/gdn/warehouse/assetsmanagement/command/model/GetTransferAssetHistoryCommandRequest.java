package com.gdn.warehouse.assetsmanagement.command.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTransferAssetHistoryCommandRequest {
   private String transferAssetNumber;
}
