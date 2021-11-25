package com.gdn.warehouse.assetsmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = SystemParam.SYSTEM_PARAMS_COLLECTIONS)
public class SystemParam extends BaseEntity{
   public static final String SYSTEM_PARAMS_COLLECTIONS = "system_params";

   private String key;
   private Object value;
}
