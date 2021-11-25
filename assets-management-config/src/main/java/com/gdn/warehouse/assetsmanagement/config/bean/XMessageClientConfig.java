package com.gdn.warehouse.assetsmanagement.config.bean;

import com.gdn.warehouse.assetsmanagement.properties.XMessageProperties;
import com.gdn.x.message.service.client.MessageTemplateDeliveryClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({XMessageProperties.class})
public class XMessageClientConfig {

   @Bean
   public MessageTemplateDeliveryClient messageTemplateDeliveryClient(XMessageProperties messageProperties) {
      return new MessageTemplateDeliveryClient(
            messageProperties.getUsername(),
            messageProperties.getPassword(),
            messageProperties.getHost(),
            Integer.valueOf(messageProperties.getPort()),
            messageProperties.getClientId(),
            messageProperties.getChannelId(),
            messageProperties.getStoreId(),
            messageProperties.getQueueName(),
            messageProperties.getExchange());
   }
}
