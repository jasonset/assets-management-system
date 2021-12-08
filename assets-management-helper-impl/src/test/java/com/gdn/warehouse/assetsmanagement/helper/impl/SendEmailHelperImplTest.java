package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import com.gdn.x.message.mq.model.MessageEmailRequest;
import com.gdn.x.message.service.client.MessageTemplateDeliveryClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class SendEmailHelperImplTest {
   @InjectMocks
   private SendEmailHelperImpl helper;

   @Mock
   private MessageTemplateDeliveryClient messageTemplateDeliveryClient;

   private SendEmailHelperRequest request;
   private Map<String,Object> map;

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      map = new HashMap<>();
      request = SendEmailHelperRequest.builder()
            .mailTemplateId("ID")
            .mailSubject("SUBJECT")
            .fromEmail("FROM@EMAIL")
            .toEmail("TO@EMAIL")
            .identifierKey("KEY")
            .identifierValue("VALUE")
            .emailVariables(map).build();
   }

   @Test
   public void sendEmail() {
      doNothing().when(messageTemplateDeliveryClient).sendMessageToQueue(any(MessageEmailRequest.class));
      helper.sendEmail(request);
      verify(messageTemplateDeliveryClient).sendMessageToQueue(any(MessageEmailRequest.class));
   }
}