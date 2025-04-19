package com.swyp.futsal.api.team.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinTeamRequest {
    @NotBlank(message = "팀 ID는 필수입니다")
    @Length(min = 36, max = 36, message = "팀 ID는 36자입니다")
    private String teamId;
} 