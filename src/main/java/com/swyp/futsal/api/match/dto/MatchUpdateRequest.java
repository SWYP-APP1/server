package com.swyp.futsal.api.match.dto;

import java.util.Optional;

import org.hibernate.validator.constraints.Length;

import com.swyp.futsal.domain.common.annotations.TimeFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchUpdateRequest {
    @NotNull
    private String id;

    @NotNull
    private Integer rounds;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private String matchDate;

    @NotNull
    private String location;

    @TimeFormat
    private String startTime;

    @TimeFormat
    private String endTime;

    private Optional<String> substituteTeamMemberId;
}
