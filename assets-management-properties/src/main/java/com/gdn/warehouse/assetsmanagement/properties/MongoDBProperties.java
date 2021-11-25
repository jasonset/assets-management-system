package com.gdn.warehouse.assetsmanagement.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Component
@ConfigurationProperties("mongodb")
public class MongoDBProperties {
   private String database;
   private String username;
   private String password;
   private ServerAddress primary;
   private ServerAddress secondary;
   private int maxConnectionsPerHost;
   private int minConnectionsPerHost;
   private int maxWaitTime;
   private int connectTimeout;
   private int readTimeout;
   private int heartbeatFrequency;
   private int minHeartbeatFrequency;
   private int maxConnectionIdleTime;
   private int maxConnectionLifeTime;

   @Data
   @Builder
   @AllArgsConstructor
   @NoArgsConstructor
   public static class ServerAddress {
      private String host;
      private int port;
   }
}
