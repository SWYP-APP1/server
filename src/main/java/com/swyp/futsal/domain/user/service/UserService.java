package com.swyp.futsal.domain.user.service;

import com.swyp.futsal.api.user.dto.*;
import com.swyp.futsal.domain.user.repository.UserRepository;
import com.swyp.futsal.exception.ErrorCode;
import com.swyp.futsal.exception.custom.CustomSystemException;
import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.provider.S3Provider;
import com.swyp.futsal.provider.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserRepository userRepository;
    private final S3Provider s3Provider;

    public UserInfo getUserInfo(String userId) {
        User user = getUserById(userId);
        Optional<PresignedUrlResponse> profileUrl = s3Provider.getDownloadPresignedUrl(user.getProfileUri());
        return new UserInfo(user, profileUrl.map(PresignedUrlResponse::getUrl));
    }

    public NicknameCheckResponse checkNickname(String nickname) {
        try {
            logger.info("Check duplicate nickname: " + nickname);
            boolean isUnique = !userRepository.existsByName(nickname);
            logger.info("Check duplicate nickname result: " + isUnique);
            return new NicknameCheckResponse(isUnique);
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.CONFLICT_NICKNAME_ALREADY_EXISTS;
            throw new CustomSystemException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    @Transactional
    public void updateUser(String userId, UpdateUserRequest request) {
        try {
            logger.info("Update user: " + userId + " " + request);
            userRepository.updateUser(userId, request);
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.NOT_FOUND_USER_ID;
            throw new CustomSystemException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    @Transactional
    public UserInfo updateNameAndSquadNumber(String userId, UpdateNameAndSquadNumberRequest request) {
        try {
            logger.info("Update user name and squad number: " + userId + " " + request);
            User user = userRepository.updateNameAndSquadNumber(userId, request);
            Optional<PresignedUrlResponse> profileUrl = s3Provider.getDownloadPresignedUrl(user.getProfileUri());
            return new UserInfo(user, profileUrl.map(PresignedUrlResponse::getUrl));
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.NOT_FOUND_USER_ID;
            throw new CustomSystemException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    @Transactional
    public void updateNotification(String userId) {
        try {
            User user = getUserById(userId);
            logger.info("Update user notification into " + !user.isNotification());
            userRepository.updateNotificationById(userId, !user.isNotification());
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.NOT_FOUND_USER_ID;
            throw new CustomSystemException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    public PresignedUrlResponse getProfilePresignedUrl() {
        String directory = String.format("users/profile");
        return s3Provider.getUploadPresignedUrl(directory);
    }

    @Transactional
    public Optional<PresignedUrlResponse> updateProfile(String userId, String profileUri) {
        try {
            userRepository.updateProfile(userId, profileUri);
            return s3Provider.getDownloadPresignedUrl(profileUri);
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.NOT_FOUND_USER_ID;
            throw new CustomSystemException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    private User getUserById(String userId) {
        try {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new CustomSystemException(ErrorCode.NOT_FOUND_USER_ID.getCode(),
                            ErrorCode.NOT_FOUND_USER_ID.getMessage()));
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.NOT_FOUND_USER_ID;
            throw new CustomSystemException(errorCode.getCode(), errorCode.getMessage());
        }
    }
}