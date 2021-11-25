package com.gdn.warehouse.assetsmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Document(collection = MaintenanceReminder.MAINTENANCE_REMINDER_COLLECTIONS)
public class MaintenanceReminder extends BaseEntity {
   public static final String MAINTENANCE_REMINDER_COLLECTIONS = "maintenance_reminders";

   private String maintenanceReminderNumber;
   private List<String> assetNumbers;
   private String assetLocation;
   private String assetPoNumber;
   private Date assetPoIssuedDate;
   private String itemCode;
   private Date scheduledDate;
   private Integer interval;
   private List<String> emailList;
   private Boolean enabled;
   private Date previousExecutionTime;
}
