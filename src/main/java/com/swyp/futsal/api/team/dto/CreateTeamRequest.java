package com.swyp.futsal.api.team.dto;

import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.common.enums.MatchType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTeamRequest {
    @NotBlank(message = "팀 이름은 필수입니다")
    private String name;

    @Size(max = 20, message = "소개는 20자를 초과할 수 없습니다")
    private String introduction;

    private String rule;

    @NotNull(message = "매치 타입은 필수입니다")
    private MatchType matchType;

    @NotNull(message = "접근 권한은 필수입니다")
    private TeamRole access;

    private Integer dues;
} 