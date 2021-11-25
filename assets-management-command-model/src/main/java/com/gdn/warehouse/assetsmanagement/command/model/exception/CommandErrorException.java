package com.gdn.warehouse.assetsmanagement.command.model.exception;

import com.blibli.oss.backend.command.exception.CommandRuntimeException;
import org.springframework.http.HttpStatus;

public class CommandErrorException extends CommandRuntimeException {
   private final HttpStatus httpStatus;
   private String errorMessageKey = "message";


   public CommandErrorException(HttpStatus httpStatus, String errorMessageKey) {
      this.httpStatus = httpStatus;
      this.errorMessageKey = errorMessageKey;
   }

   public CommandErrorException(String message, HttpStatus httpStatus) {
      super(message);
      this.httpStatus = httpStatus;
   }

   public CommandErrorException(String errorMessageKey, String message, HttpStatus httpStatus) {
      super(message);
      this.errorMessageKey = errorMessageKey;
      this.httpStatus = httpStatus;
   }

   public HttpStatus getHttpStatus() {
      return this.httpStatus;
   }

   public String getReasonPhrase() {
      return this.httpStatus.getReasonPhrase();
   }

   public String getErrorMessageKey() {
      return errorMessageKey;
   }
}
