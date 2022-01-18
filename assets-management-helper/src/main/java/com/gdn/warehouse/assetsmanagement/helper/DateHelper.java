package com.gdn.warehouse.assetsmanagement.helper;

import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;

public interface DateHelper {
   Mono<Calendar> validateScheduledDate(Long scheduledDate);
   String convertDateForEmail(Date date);
}
