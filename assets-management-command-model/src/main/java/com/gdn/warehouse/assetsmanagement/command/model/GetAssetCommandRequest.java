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
public class GetAssetCommandRequest {
   private String assetNumberFilter;
   private String organisationFilter;
   private String vendorFilter;
   private String itemCodeFilter;
   private String locationFilter;
   private String statusFilter;
   private String categoryFilter;
   @NotNull(message = "NotNull")
   @Min(value = 1)
   private Integer limit;
   @NotNull(message = "NotNull")
   @Min(value = 1)
   private Integer page;
   private List<SortBy> sortBy;
}
