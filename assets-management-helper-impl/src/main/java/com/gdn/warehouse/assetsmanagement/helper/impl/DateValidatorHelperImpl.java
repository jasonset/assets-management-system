package com.gdn.warehouse.assetsmanagement.helper.impl;

import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import com.gdn.warehouse.assetsmanagement.helper.DateValidatorHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class DateValidatorHelperImpl implements DateValidatorHelper {
   @Override
   public Mono<Calendar> validateScheduledDate(Long scheduledDate) {
      Date date = new Date(scheduledDate);
      Calendar now = Calendar.getInstance();
      if(date.before(now.getTime())){
         return Mono.defer(()->Mono.error(new CommandErrorException("Scheduled Date can't be before today's date!", HttpStatus.BAD_REQUEST)));
      }else {
         now.setTime(date);
         return Mono.fromSupplier(()->now);
      }
   }
}
