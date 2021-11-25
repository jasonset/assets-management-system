package com.gdn.warehouse.assetsmanagement.helper;

import com.gdn.warehouse.assetsmanagement.helper.model.SendEmailHelperRequest;
import reactor.core.publisher.Mono;

public interface SendEmailHelper {
   void sendEmail(SendEmailHelperRequest request);
}
