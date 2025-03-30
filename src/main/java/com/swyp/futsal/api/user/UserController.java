package com.swyp.futsal.api.user;

import com.swyp.futsal.api.user.dto.*;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.domain.user.service.UserService;
import com.swyp.futsal.provider.PresignedUrlResponse;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final AuthService authService;
  private final UserService userService;

  @GetMapping("/me")
  public ApiResponse<UserInfo> getUserMe(
      @RequestHeader("Authorization") String authorization) {
    String userId = getUserIdByHeader(authorization);
    return ApiResponse.success(userService.getUserInfo(userId));
  }

  @GetMapping("/check-nickname")
  public ApiResponse<NicknameCheckResponse> checkNickname(
      @RequestParam String nickname) {
    return ApiResponse.success(userService.checkNickname(nickname));
  }

  @PatchMapping("")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ApiResponse<Void> updateUser(
      @RequestHeader("Authorization") String authorization,
      @Valid @RequestBody UpdateUserRequest request) {
    String userId = getUserIdByHeader(authorization);
    userService.updateUser(userId, request);
    return ApiResponse.success(null);
  }

  @PatchMapping("/me")
  public ApiResponse<UserInfo> updateNameAndSquadNumber(
      @RequestHeader("Authorization") String authorization,
      @Valid @RequestBody UpdateNameAndSquadNumberRequest request) {
    String userId = getUserIdByHeader(authorization);

    return ApiResponse.success(userService.updateNameAndSquadNumber(userId, request));
  }

  @PatchMapping("/notification")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ApiResponse<Void> updateNotification(
      @RequestHeader("Authorization") String authorization) {

    String userId = getUserIdByHeader(authorization);
    userService.updateNotification(userId);
    return ApiResponse.success(null);
  }

  @GetMapping("/profile-presigned-url")
  public ApiResponse<PresignedUrlResponse> getProfilePresignedUrl() {
    return ApiResponse.success(userService.getProfilePresignedUrl());
  }

  @PatchMapping("/profile")
  public ApiResponse<Optional<PresignedUrlResponse>> updateProfile(
      @RequestHeader("Authorization") String authorization,
      @RequestBody UpdateProfileRequest request) {
    String userId = getUserIdByHeader(authorization);
    return ApiResponse.success(userService.updateProfile(userId, request.getUri()));
  }

  private String getUserIdByHeader(String authorization) {
    String token = RequestUtil.getAuthorizationToken(authorization);
    return authService.getUserId(token);
  }
}
