package com.swyp.futsal.api.team.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinTeamRequest {
    @NotBlank(message = "팀 ID는 필수입니다")
    private String teamId;
} 