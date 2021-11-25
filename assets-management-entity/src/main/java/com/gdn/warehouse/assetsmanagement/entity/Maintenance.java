package com.gdn.warehouse.assetsmanagement.entity;

import com.gdn.warehouse.assetsmanagement.enums.MaintenanceStatus;
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
@Document(collection = Maintenance.MAINTENANCE_COLLECTIONS)
public class Maintenance extends BaseEntity{

   public static final String MAINTENANCE_COLLECTIONS = "maintenances";

   private String maintenanceNumber;
   private String requester;
   private List<String> assetNumbers;
   private MaintenanceStatus status;
   private String itemCode;
   private String location;
   private String warehouseManagerEmail;
//   private String poNumber;
//   private Date poIssuedDate;
   private Integer maintenanceFee;
   private Date tanggalKerusakan;
   private Date tanggalLaporan;
   private String deskripsiKerusakan;
   private Date quoSubmit;
   private Date poSubmit;
   private Date poApproved;
   private Date tanggalService;
   private Date tanggalNormal;
   private Boolean reject;
   private String alasanReject;
   private String notes;

}
