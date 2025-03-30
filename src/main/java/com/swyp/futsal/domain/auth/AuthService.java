package com.swyp.futsal.domain.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.swyp.futsal.domain.user.repository.UserRepository;
import com.swyp.futsal.exception.ErrorCode;
import com.swyp.futsal.exception.custom.CustomSystemException;
import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.api.app.dto.LoginRequest;
import com.swyp.futsal.api.app.dto.TokenResponse;
import com.swyp.futsal.domain.common.enums.Gender;
import com.swyp.futsal.domain.common.enums.Platform;
import com.swyp.futsal.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FirebaseAuth firebaseAuth;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        try {
            log.info("Login attempt - Platform: {}", request.getPlatform());
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(request.getToken());
            log.debug("Firebase token verification completed - UID: {}", decodedToken.getUid());

            User user = userRepository.findByPlatformAndUid(request.getPlatform(), decodedToken.getUid())
                    .orElseGet(() -> {
                        log.info("Creating new user - Platform: {}, UID: {}", request.getPlatform(),
                                decodedToken.getUid());
                        return createUser(request.getPlatform(), decodedToken);
                    });

            String accessToken = jwtTokenProvider.createAccessToken(user.getId());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
            log.info("Token generation completed - UserId: {}", user.getId());

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .isNew(user.getGender() == Gender.NONE) // Set
                    .build();
        } catch (FirebaseAuthException e) {
            log.error("Firebase authentication failed", e);
            throw new IllegalArgumentException("Invalid Firebase token", e);
        }
    }

    public TokenResponse refresh(String refreshToken) {
        log.info("Token refresh requested");
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String userId = jwtTokenProvider.getUserId(refreshToken);
        log.debug("Refresh token validation completed - UserId: {}", userId);

        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .isNew(false)
                .build();
    }

    public String getUserId(String token) {
        try {
            return jwtTokenProvider.getUserId(token);
        } catch (Exception e) {
            log.error("Error getting user ID from token", e);
            ErrorCode errorCode = ErrorCode.UNAUTHORIZED_TOKEN_AUTHENTICATION_FAILED;
            throw new CustomSystemException(errorCode.getCode(), errorCode.getMessage());
        }
    }

    private User createUser(Platform platform, FirebaseToken decodedToken) {
        log.debug("Starting user creation - Platform: {}, Email: {}", platform, decodedToken.getEmail());
        User user = User.builder()
                .platform(platform)
                .uid(decodedToken.getUid())
                .email(decodedToken.getEmail())
                .name(decodedToken.getName())
                .build();
        return userRepository.save(user);
    }
}