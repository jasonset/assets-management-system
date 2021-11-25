package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.helper.SendEmailHelper;
import com.gdn.x.message.mq.model.MessageEmailRequest;
import com.gdn.x.message.service.client.MessageTemplateDeliveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;

@Slf4j
@Service
public class SendEmailHelperImpl implements SendEmailHelper {

   @Autowired
   private MessageTemplateDeliveryClient messageTemplateDeliveryClient;

   @Override
   public void sendEmail(SendEmailHelperRequest request) {
      MessageEmailRequest messageEmailRequest = new MessageEmailRequest();
      messageEmailRequest.setMessageId(request.getMailTemplateId());
      messageEmailRequest.setMessageSubject(request.getMailSubject());
      messageEmailRequest.setMessageFrom(request.getFromEmail());
      messageEmailRequest.setMessageTo(request.getToEmail());
      messageEmailRequest.setMessageIdentifierKey(request.getIdentifierKey());
      messageEmailRequest.setMessageIdentifierValue(request.getIdentifierValue());
      messageEmailRequest.setVariables(request.getEmailVariables());
      messageTemplateDeliveryClient.sendMessageToQueue(messageEmailRequest);
   }
}
