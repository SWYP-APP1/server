package com.swyp.futsal.api.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNameAndSquadNumberRequest {
    @NotNull
    private String name;

    @NotNull
    private Integer squadNumber;
}