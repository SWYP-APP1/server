package com.swyp.futsal.api.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.swyp.futsal.domain.common.enums.Gender;

@Getter
@NoArgsConstructor
public class UpdateUserRequest {
    private String nickname;
    private LocalDate birthDate;
    private Gender gender;
    private boolean agreement;
    private boolean notification;
}