package com.swyp.futsal.api.user.dto;

import com.swyp.futsal.domain.common.enums.Gender;
import com.swyp.futsal.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
public class UserInfo {
  private final String email;
  private final String name;
  private final Optional<String> birthDate;
  private final Gender gender;
  private final Integer squadNumber;
  private final boolean notification;
  private final boolean agreement;
  private final Optional<String> profileUrl;
  private final LocalDateTime createdTime;

  public UserInfo(User user, Optional<String> profileUrl) {
    this.email = user.getEmail();
    this.name = user.getName();
    this.birthDate = Optional.ofNullable(user.getBirthDate());
    this.gender = user.getGender();
    this.squadNumber = user.getSquadNumber();
    this.notification = user.isNotification();
    this.agreement = user.isAgreement();
    this.profileUrl = profileUrl;
    this.createdTime = user.getCreatedTime();
  }
}
