package com.swyp.futsal.api.team.dto;

import com.swyp.futsal.domain.common.enums.MatchType;
import com.swyp.futsal.domain.common.enums.TeamRole;

import lombok.Getter;

@Getter
public class UpdateTeamRequest {
    private String name;
    private String description;
    private String rule;
    private MatchType matchType;
    private TeamRole access;
    private Integer dues;
}
