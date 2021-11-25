package com.gdn.warehouse.assetsmanagement.command.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllWarehouseCommandRequest {
   private String codeFilter;
   private String nameFilter;
   private Integer limit;
   private Integer page;
   private String sortBy = "code";
   private String sortOrder = "asc";
}
