package com.gdn.warehouse.assetsmanagement.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTransferAssetHistoryWebResponse {
   private String transferAssetNumber;
   private String status;
   private Date updatedDate;
   private String updatedBy;
}
