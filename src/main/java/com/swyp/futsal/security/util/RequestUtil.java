package com.swyp.futsal.security.util;

import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

  public static String getAuthorizationToken(String header) {
    if (header == null || !header.startsWith("Bearer ")) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED_TOKEN_AUTHENTICATION_FAILED);
    }

    return header.substring("Bearer ".length());
  }

  public static String getAuthorizationToken(HttpServletRequest request) {
    return getAuthorizationToken(request.getHeader("Authorization"));
  }
}
