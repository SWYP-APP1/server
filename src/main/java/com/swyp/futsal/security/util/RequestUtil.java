package com.swyp.futsal.security.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

  public static String getAuthorizationToken(String header) {
    if (header == null || !header.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Invalid authorization header");
    }

    // Remove "Bearer " prefix
    return header.substring("Bearer ".length());
  }

  public static String getAuthorizationToken(HttpServletRequest request) {
    return getAuthorizationToken(request.getHeader("Authorization"));
  }
}
