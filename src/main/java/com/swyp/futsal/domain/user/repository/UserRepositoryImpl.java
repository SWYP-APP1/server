package com.swyp.futsal.domain.user.repository;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.swyp.futsal.api.user.dto.*;
import com.swyp.futsal.domain.common.enums.Platform;
import com.swyp.futsal.domain.user.entity.QUser;
import com.swyp.futsal.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final static QUser qUser = QUser.user;

  @Override
  public Optional<User> findByEmail(String email) {
    return Optional.ofNullable(jpaQueryFactory
        .selectFrom(qUser)
        .where(qUser.email.eq(email))
        .fetchOne());
  }

  @Override
  public Optional<User> findByUid(String uid) {
    return Optional.ofNullable(jpaQueryFactory
        .selectFrom(qUser)
        .where(qUser.uid.eq(uid))
        .fetchOne());
  }

  @Override
  public Optional<User> findByPlatformAndUid(Platform platform, String uid) {
    return Optional.ofNullable(jpaQueryFactory
        .selectFrom(qUser)
        .where(qUser.platform.eq(platform), qUser.uid.eq(uid))
        .fetchOne());
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaQueryFactory
        .selectFrom(qUser)
        .where(qUser.email.eq(email))
        .fetchFirst() != null;
  }

  @Override
  public boolean existsByUid(String uid) {
    return jpaQueryFactory
        .selectFrom(qUser)
        .where(qUser.uid.eq(uid))
        .fetchFirst() != null;
  }

  @Override
  public boolean existsByName(String name) {
    return jpaQueryFactory
        .selectFrom(qUser)
        .where(qUser.name.eq(name))
        .fetchFirst() != null;
  }

  @Override
  public void updateUser(String userId, UpdateUserRequest request) {
    jpaQueryFactory
        .update(qUser)
        .set(qUser.name, request.getNickname())
        .set(qUser.birthDate, request.getBirthDate())
        .set(qUser.gender, request.getGender())
        .set(qUser.agreement, request.isAgreement())
        .set(qUser.notification, request.isNotification())
        .where(qUser.id.eq(userId))
        .execute();
  }

  @Override
  public void updateNotificationById(String userId, boolean notification) {
    jpaQueryFactory
        .update(qUser)
        .set(qUser.notification, notification)
        .where(qUser.id.eq(userId))
        .execute();
  }

  @Override
  public User updateNameAndSquadNumber(String userId, UpdateNameAndSquadNumberRequest request) {
    jpaQueryFactory
        .update(qUser)
        .set(qUser.name, request.getName())
        .set(qUser.squadNumber, request.getSquadNumber())
        .where(qUser.id.eq(userId))
        .execute();

    return jpaQueryFactory
        .selectFrom(qUser)
        .where(qUser.id.eq(userId))
        .fetchOne();
  }

  @Override
  public void updateProfile(String userId, String profileUri) {
    jpaQueryFactory
        .update(qUser)
        .set(qUser.profileUri, profileUri)
        .where(qUser.id.eq(userId))
        .execute();
  }
}
