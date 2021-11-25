package com.gdn.warehouse.assetsmanagement.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("schedule")
public class ScheduleProperties {
   private String groupName = "warehouse-assets-management";
}
