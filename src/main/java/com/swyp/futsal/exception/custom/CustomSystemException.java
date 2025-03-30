package com.swyp.futsal.exception.custom;

import lombok.Getter;

@Getter
public class CustomSystemException extends RuntimeException {

  String code;

  public CustomSystemException(String code, String message) {
    super(message);
    this.code = code;
  }

}
