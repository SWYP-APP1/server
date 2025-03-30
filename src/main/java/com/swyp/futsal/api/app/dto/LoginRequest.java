package com.swyp.futsal.api.app.dto;

import com.swyp.futsal.domain.common.enums.Platform;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    private String token;
    private Platform platform;
}