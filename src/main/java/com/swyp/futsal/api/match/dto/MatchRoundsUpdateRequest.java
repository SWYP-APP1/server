package com.swyp.futsal.api.match.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MatchRoundsUpdateRequest {
    @NotNull(message = "rounds must not be null")
    @Min(value = 1, message = "rounds must be greater than 0")
    private Integer rounds;
} 