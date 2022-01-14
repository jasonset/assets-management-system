package com.gdn.warehouse.assetsmanagement.helper;

import reactor.core.publisher.Mono;

import java.util.Calendar;

public interface DateValidatorHelper {
   Mono<Calendar> validateScheduledDate(Long scheduledDate);
}
