package com.swyp.futsal.domain.user.repository;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swyp.futsal.domain.common.enums.Platform;
import com.swyp.futsal.domain.user.entity.QUser;
import com.swyp.futsal.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final static QUser qUser = QUser.user;

  private User findTestUser(String uid) {
    return jpaQueryFactory
        .selectFrom(qUser)
        .where(qUser.uid.eq(uid))
        .fetchOne();
  }

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
}
