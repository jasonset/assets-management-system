package com.gdn.warehouse.assetsmanagement.entity;

import com.gdn.warehouse.assetsmanagement.enums.AssetCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Document(collection = Item.ITEM_COLLECTIONS)
public class Item extends BaseEntity{

   public static final String ITEM_COLLECTIONS = "items";

   private String itemCode;
   private String itemName;
   private AssetCategory category;
}
