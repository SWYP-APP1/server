package com.swyp.futsal.api.team.dto;


import java.time.LocalDateTime;
import java.util.Optional;

import com.swyp.futsal.domain.common.enums.TeamRole;

import lombok.Getter;

@Getter
public class GetMyTeamResponse {
    private String id;
    private String teamMemberId;
    private String name;
    private Optional<String> logoUrl;
    private TeamRole role;
    private LocalDateTime createdTime;

    public GetMyTeamResponse(String id, String teamMemberId, String name, Optional<String> logoUrl, TeamRole role, LocalDateTime createdTime) {
        this.id = id;
        this.teamMemberId = teamMemberId;
        this.name = name;
        this.logoUrl = logoUrl;
        this.role = role;
        this.createdTime = createdTime;
    }
}
