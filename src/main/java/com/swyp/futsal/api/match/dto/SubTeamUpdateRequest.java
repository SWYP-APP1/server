package com.swyp.futsal.api.match.dto;

import java.util.List;

import com.swyp.futsal.domain.common.enums.SubTeam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubTeamUpdateRequest {
    private String matchId;
    private List<String> ids;
    private SubTeam subTeam;
} 