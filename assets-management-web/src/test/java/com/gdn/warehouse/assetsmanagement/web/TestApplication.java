package com.gdn.warehouse.assetsmanagement.web;

import com.gdn.warehouse.assetsmanagement.properties.ReactorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
      ReactorProperties.class
})
public class TestApplication {
   public static void main(String[] args) {
      SpringApplication.run(TestApplication.class);
   }
}
