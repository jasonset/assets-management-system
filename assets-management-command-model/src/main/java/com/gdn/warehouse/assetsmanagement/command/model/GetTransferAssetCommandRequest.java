package com.gdn.warehouse.assetsmanagement.command.model;

import com.blibli.oss.backend.common.model.request.SortBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTransferAssetCommandRequest {
   private String transferAssetNumberFilter;
   private String assetNumberFilter;
   private String originFilter;
   private String destinationFilter;
   private String itemCodeFilter;
   private String statusFilter;
   private String transferAssetTypeFilter;
   private String referenceNumberFilter;
   @NotNull(message = "NotNull")
   @Min(value = 1)
   private Integer limit;
   @NotNull(message = "NotNull")
   @Min(value = 1)
   private Integer page;
   private List<SortBy> sortBy;
}
