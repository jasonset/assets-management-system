package com.gdn.warehouse.assetsmanagement.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("reactor")
public class ReactorProperties {
   private StreamConfig external;
   private StreamConfig internal;

   @Data
   public static class StreamConfig {
      private int bufferSize = Integer.MAX_VALUE;
   }

}
