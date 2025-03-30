package com.swyp.futsal.api.team.dto;

import com.swyp.futsal.domain.team.entity.Team;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamResponse {
    private String id;
    private String name;
    private LocalDateTime createdTime;

    public TeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.createdTime = team.getCreatedTime();
    }
} 