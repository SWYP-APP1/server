package com.swyp.futsal.exception.custom;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

  String code;

  public BizException(String code, String message) {
    super(message);
    this.code = code;
  }

}
