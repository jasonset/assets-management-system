package com.gdn.warehouse.assetsmanagement.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetItemWebResponse {
   private String code;
   private String name;
   private String category;
}
