package com.swyp.futsal.api.user.dto;


import com.swyp.futsal.annotation.PasswordEncryption;
import com.swyp.futsal.domain.user.entity.User;
import lombok.Data;

@Data
public class UserInfo {
  private String uid;
  private String email;
  @PasswordEncryption//테스트
  private String nickname;

  public UserInfo(User customUser) {
    this.uid = customUser.getUid();
    this.email = customUser.getEmail();
  }
}
