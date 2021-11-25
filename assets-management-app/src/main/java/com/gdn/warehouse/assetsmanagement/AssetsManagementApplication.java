package com.gdn.warehouse.assetsmanagement;

import com.gdn.warehouse.assetsmanagement.properties.XMessageProperties;
import com.gdn.warehouse.assetsmanagement.properties.MongoDBProperties;
import com.gdn.warehouse.assetsmanagement.properties.ScheduleProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
      MongoDBProperties.class,
      XMessageProperties.class,
      ScheduleProperties.class})
@SpringBootApplication
public class AssetsManagementApplication {
   public static void main(String[] args) {
      SpringApplication.run(AssetsManagementApplication.class, args);
   }
}
