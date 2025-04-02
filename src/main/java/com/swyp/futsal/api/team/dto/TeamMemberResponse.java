package com.swyp.futsal.api.team.dto;

import com.swyp.futsal.domain.team.entity.TeamMember;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TeamMemberResponse {
    private final String id;
    private final String teamId;
    private final String teamName;
    private final String logoUrl;
    private final String role;
    private final LocalDateTime createdTime;

    public TeamMemberResponse(TeamMember teamMember) {
        this.id = teamMember.getId();
        this.teamId = teamMember.getTeam().getId();
        this.teamName = teamMember.getTeam().getName();
        this.logoUrl = teamMember.getTeam().getLogoUri();
        this.role = teamMember.getRole().name();
        this.createdTime = teamMember.getCreatedTime();
    }
} 