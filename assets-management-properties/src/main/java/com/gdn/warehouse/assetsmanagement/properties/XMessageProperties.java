package com.gdn.warehouse.assetsmanagement.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("message")
public class XMessageProperties {
   private String host;
   private String port;
   private String username;
   private String password;
   private String storeId;
   private String clientId;
   private String channelId;
   private String queueName;
   private String exchange;
}
