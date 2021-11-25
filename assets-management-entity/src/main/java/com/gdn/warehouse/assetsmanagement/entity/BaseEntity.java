package com.gdn.warehouse.assetsmanagement.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEntity {
   @Id
   @JsonSerialize(using = ToStringSerializer.class)
   protected ObjectId id;

   @Version
   protected Long version;

   @CreatedDate
   protected Date createdDate;

   @CreatedBy
   protected String createdBy;

   @LastModifiedDate
   protected Date lastModifiedDate;

   @LastModifiedBy
   protected String lastModifiedBy;
}
