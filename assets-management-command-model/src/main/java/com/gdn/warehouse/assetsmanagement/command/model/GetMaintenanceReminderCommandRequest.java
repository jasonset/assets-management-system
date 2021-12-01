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
public class GetMaintenanceReminderCommandRequest {
   private String maintenanceReminderNumberFilter;
   private String assetNumberFilter;
   private String itemCodeFilter;
   private Long scheduledDateFilter;
   private Long previousExecutionTimeFilter;
   private Integer intervalFilter;
   private String emailFilter;
   @NotNull(message = "NotNull")
   @Min(value = 1)
   private Integer limit;
   @NotNull(message = "NotNull")
   @Min(value = 1)
   private Integer page;
   private List<SortBy> sortBy;
}
