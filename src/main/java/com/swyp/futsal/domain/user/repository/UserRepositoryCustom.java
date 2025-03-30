package com.swyp.futsal.domain.user.repository;

import java.util.Optional;

import com.swyp.futsal.domain.common.enums.Platform;
import com.swyp.futsal.domain.user.entity.User;

public interface UserRepositoryCustom {
    Optional<User> findByEmail(String email);

    Optional<User> findByUid(String uid);

    Optional<User> findByPlatformAndUid(Platform platform, String uid);

    boolean existsByEmail(String email);

    boolean existsByUid(String uid);

    boolean existsByName(String name);
}
