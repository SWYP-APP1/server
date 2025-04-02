package com.swyp.futsal.api.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.google.firebase.database.annotations.NotNull;
import com.swyp.futsal.domain.common.enums.Gender;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class UpdateUserRequest {

    @NotNull
    private String nickname;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜 형식은 yyyy-MM-dd 여야 합니다")
    private String birthDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull
    private boolean agreement;

    @NotNull
    private boolean notification;
}