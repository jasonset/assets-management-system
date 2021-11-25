package com.gdn.warehouse.assetsmanagement.web.model.request.generic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FilterAndPageRequest<T,E> {
   private T filters;
   private E sorts;
   @JsonProperty("item_per_page")
   private Integer itemPerPage;
   private Integer page;
}
