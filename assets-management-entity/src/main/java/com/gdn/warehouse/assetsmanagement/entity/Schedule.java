package com.gdn.warehouse.assetsmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document(collection = Schedule.SCHEDULES_COLLECTIONS)
public class Schedule extends BaseEntity {
   public static final String SCHEDULES_COLLECTIONS = "schedules";

   private String identifier;
   private String payload;
   private String topicName;
   private TimeUnit timeUnit;
   private Integer interval;
   private Date nextScheduledTime;
   private Date lastExecutionTime;
   private Long previousDuration;
   private Boolean enabled;

   @Builder
   public Schedule(ObjectId id, Long version, Date createdDate, String createdBy, Date lastModifiedDate,
                   String lastModifiedBy, String identifier, String payload,
                   String topicName, TimeUnit timeUnit, Integer interval, Date nextScheduledTime,
                   Date lastExecutionTime, Long previousDuration, Boolean enabled) {
      super(id, version, createdDate, createdBy, lastModifiedDate, lastModifiedBy);
      this.identifier = identifier;
      this.payload = payload;
      this.topicName = topicName;
      this.timeUnit = timeUnit;
      this.interval = interval;
      this.nextScheduledTime = nextScheduledTime;
      this.lastExecutionTime = lastExecutionTime;
      this.previousDuration = previousDuration;
      this.enabled = enabled;
   }
}
