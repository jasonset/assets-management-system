package com.gdn.warehouse.assetsmanagement.command.model;

import com.blibli.oss.backend.common.model.request.SortBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllItemWithFilterCommandRequest {
   private String codeFilter;
   private String nameFilter;
   private String categoryFilter;
   private Integer limit;
   private Integer page;
   private List<SortBy> sortBy;
}
