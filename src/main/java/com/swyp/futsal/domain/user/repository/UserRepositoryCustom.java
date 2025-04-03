package com.swyp.futsal.domain.user.repository;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.Tuple;
import com.swyp.futsal.api.user.dto.*;
import com.swyp.futsal.domain.common.enums.Platform;
import com.swyp.futsal.domain.user.entity.User;

public interface UserRepositoryCustom {
    List<Tuple> findAllWithTeamMemberByTeamMemberIds(List<String> teamMemberIds);
    Optional<User> findByEmail(String email);
    Optional<User> findByUid(String uid);
    Optional<User> findByPlatformAndUid(Platform platform, String uid);
    boolean existsByEmail(String email);
    boolean existsByUid(String uid);
    boolean existsByName(String name);
    void updateUser(String userId, UpdateUserRequest request);
    void updateNotificationById(String userId, boolean notification);
    User updateNameAndSquadNumber(String userId, UpdateNameAndSquadNumberRequest request);
    void updateProfile(String userId, String profileUri);
}
