package com.gdn.warehouse.assetsmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Document(collection = Warehouse.WAREHOUSE_COLLECTIONS)
public class Warehouse extends BaseEntity{

   public static final String WAREHOUSE_COLLECTIONS = "warehouses";

   private String warehouseName;
   private String warehouseCode;
   private String email;
}
