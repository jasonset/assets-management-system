package com.gdn.warehouse.assetsmanagement.entity;

import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Document(collection = MaintenanceHistory.MAINTENANCE_HISTORY_COLLECTIONS)
public class MaintenanceHistory {

   public static final String MAINTENANCE_HISTORY_COLLECTIONS = "maintenance_histories";

   private String maintenanceNumber;
   private MaintenanceStatus status;
   private Date updatedDate;
   private String updatedBy;

}
