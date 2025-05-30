package com.swyp.futsal.api.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {

    @NotNull
    @Pattern(regexp = "^ncp://.*", message = "INVALID_URI")
    private String uri;
}