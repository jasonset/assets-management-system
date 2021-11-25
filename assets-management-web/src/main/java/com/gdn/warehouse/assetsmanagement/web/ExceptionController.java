package com.gdn.warehouse.assetsmanagement.web;

import com.blibli.oss.backend.command.controller.CommandErrorController;
import com.blibli.oss.backend.command.exception.CommandValidationException;
import com.blibli.oss.backend.common.model.response.Response;
import com.blibli.oss.backend.common.webflux.controller.CommonErrorController;

import com.gdn.warehouse.assetsmanagement.command.model.exception.CommandErrorException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by tommy.setiawan on 3/9/2020.
 */
@Slf4j
@RestControllerAdvice
public class ExceptionController implements CommandErrorController, MessageSourceAware {

  @Getter
  @Setter
  private MessageSource messageSource;

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  public MessageSource getMessageSource() {
    return messageSource;
  }

  @Override
  public ResponseEntity<Response<Object>> commandValidationException(CommandValidationException e) {
    this.getLogger().warn("CommandValidationException: {}",
        CommonErrorController.from(e.getConstraintViolations()));
    Response<Object> response = new Response();
    response.setCode(HttpStatus.BAD_REQUEST.value());
    response.setStatus(HttpStatus.BAD_REQUEST.name());
    response.setErrors(CommonErrorController.from(e.getConstraintViolations()));
    return ResponseEntity.ok().body(response);
  }

  @ExceptionHandler({CommandErrorException.class})
  public ResponseEntity<Response<Object>> commandErrorException(CommandErrorException e) {
    this.getLogger().warn("CommandErrorException: {}", e);
    Response<Object> response = new Response();
    response.setCode(e.getHttpStatus().value());
    response.setStatus(e.getHttpStatus().name());
    response.setErrors(toErrors(e.getErrorMessageKey(), e.getMessage()));
    return ResponseEntity.ok().body(response);
  }

  private Map<String, List<String>> toErrors(String errorMessageKey, String message) {
    Map<String, List<String>> errorMaps = new HashMap<>();
    errorMaps.put(errorMessageKey, Collections.singletonList(message));
    return errorMaps;
  }
}
