package com.swyp.futsal.api.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.swyp.futsal.api.user.dto.RegisterInfo;
import com.swyp.futsal.api.user.dto.UserInfo;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.domain.user.service.UserService;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final AuthService authService;
  private final UserService userService;

  // @PostMapping("")
  // public ApiResponse<UserInfo> register(@RequestHeader("Authorization") String
  // authorization,
  // @RequestBody RegisterInfo registerInfo) {

  // String userId = getUserIdByHeader(authorization);
  // User registeredUser = userService.register(
  // userId, registerInfo.getNickname());
  // return ApiResponse.success(new UserInfo(registeredUser));
  // }

  @GetMapping("/me")
  public ApiResponse<UserInfo> getUserMe(Authentication authentication) {
    User customUser = ((User) authentication.getPrincipal());
    return ApiResponse.success(new UserInfo(customUser));
  }

  // @PatchMapping
  // public ResponseEntity<Void> updateUser(
  // @LoginUser Long userId,
  // @RequestBody UpdateUserRequest request) {
  // userService.updateUser(userId, request);
  // return ResponseEntity.noContent().build();
  // }

  // @GetMapping("/check-nickname")
  // public ResponseEntity<NicknameCheckResponse> checkNickname(
  // @RequestParam String nickname) {
  // return ResponseEntity.ok(userService.checkNickname(nickname));
  // }

  // @GetMapping("/profile-presigned-url")
  // public ResponseEntity<PresignedUrlResponse> getProfilePresignedUrl(
  // @LoginUser Long userId) {
  // return ResponseEntity.ok(userService.getProfilePresignedUrl(userId));
  // }

  // @PatchMapping("/profile")
  // public ResponseEntity<PresignedUrlResponse> updateProfile(
  // @ Long userId,
  // @RequestBody UpdateProfileRequest request) {
  // return ResponseEntity.ok(userService.updateProfile(userId, request));
  // }

  private String getUserIdByHeader(String authorization) {
    String token = RequestUtil.getAuthorizationToken(authorization);
    return authService.getUserId(token);
  }
}
