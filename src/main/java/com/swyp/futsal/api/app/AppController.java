package com.swyp.futsal.api.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.swyp.futsal.api.app.dto.LoginRequest;
import com.swyp.futsal.api.app.dto.TokenResponse;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AppController {

  private final AuthService authService;

  @GetMapping
  public ApiResponse<String> getAppVersion() {
    return ApiResponse.success("1.0.0");
  }

  @PostMapping("/login")
  public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
    return ApiResponse.success(authService.login(request));
  }

  @PostMapping("/refresh")
  public ApiResponse<TokenResponse> refresh(@RequestHeader("Authorization") String authorization) {
    String token = RequestUtil.getAuthorizationToken(authorization);
    return ApiResponse.success(authService.refresh(token));
  }

  @GetMapping("/hello")
  public String hello() {
    return "hello";
  }
}
