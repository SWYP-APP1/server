package com.swyp.futsal.exception;

import com.swyp.futsal.exception.custom.BizException;
import com.swyp.futsal.exception.custom.CustomSystemException;
import com.swyp.futsal.util.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(value = { BizException.class })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> handleCustomException(BizException ex) {
    return ApiResponse.fail(ex.getCode(), ex.getMessage(), null);
  }

  @ExceptionHandler(value = { CustomSystemException.class })
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<Void> handleCustomException(CustomSystemException ex) {
    return ApiResponse.fail(ex.getCode(), ex.getMessage(), null);
  }

  @ExceptionHandler(value = { BusinessException.class })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResponse<Void> handleBusinessException(BusinessException ex) {
    return ApiResponse.fail(ex.getErrorCode().name(), ex.getMessage(), null);
  }

  @ExceptionHandler(value = { Exception.class })
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<Void> handleException(Exception ex) {
    log.error("", ex);
    return ApiResponse.fail("ERROR", ex.getMessage(), null);
  }
}