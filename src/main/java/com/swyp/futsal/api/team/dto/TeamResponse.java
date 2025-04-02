package com.swyp.futsal.api.team.dto;

import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.common.enums.MatchType;
import com.swyp.futsal.domain.common.enums.TeamRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamResponse {
    private String id;
    private String name;
    private String introduction;
    private String rule;
    private MatchType matchType;
    private TeamRole access;
    private Integer dues;
    private LocalDateTime createdTime;

    public TeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.introduction = team.getIntroduction();
        this.rule = team.getRule();
        this.matchType = team.getMatchType();
        this.access = team.getAccess();
        this.dues = team.getDues();
        this.createdTime = team.getCreatedTime();
    }
} 