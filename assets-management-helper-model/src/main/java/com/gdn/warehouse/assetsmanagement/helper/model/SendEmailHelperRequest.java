package com.gdn.warehouse.assetsmanagement.helper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailHelperRequest {
   private String mailTemplateId;
   private String mailSubject;
   private String fromEmail;
   private String toEmail;
   private String identifierKey;
   private String identifierValue;
   private Map<String, Object> emailVariables;
}
