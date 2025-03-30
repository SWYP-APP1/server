package com.swyp.futsal.domain.user;
// package com.swyp.futsal.api.user;

// import com.swyp.futsal.api.user.dto.*;
// import com.swyp.futsal.domain.user.UserRepository;
// import com.swyp.futsal.domain.user.entity.User;
// import com.swyp.futsal.provider.S3Provider;
// import com.swyp.futsal.provider.PresignedUrlResponse;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// @Service
// @RequiredArgsConstructor
// @Transactional(readOnly = true)
// public class UserServiceImpl {

// private final UserRepository userRepository;
// private final S3Provider s3Provider;

// @Transactional
// public void updateUser(Long userId, UpdateUserRequest request) {
// User user = getUserById(userId);
// user.updateUserInfo(
// request.getNickname(),
// request.getBirthDate(),
// request.getGender(),
// request.isAgreement(),
// request.isNotification());
// }

// public NicknameCheckResponse checkNickname(String nickname) {
// boolean isUnique = !userRepository.existsByNickname(nickname);
// return new NicknameCheckResponse(isUnique);
// }

// public PresignedUrlResponse getProfilePresignedUrl(Long userId) {
// getUserById(userId); // validate user exists
// return s3Provider.getUploadPresignedUrl("images/profile");
// }

// @Transactional
// public PresignedUrlResponse updateProfile(Long userId, UpdateProfileRequest
// request) {
// User user = getUserById(userId);
// user.updateProfile(request.getUri());
// return s3Provider.getDownloadPresignedUrl(request.getUri());
// }

// private User getUserById(Long userId) {
// return userRepository.findById(userId)
// .orElseThrow(() -> new IllegalArgumentException("User not found"));
// }
// }