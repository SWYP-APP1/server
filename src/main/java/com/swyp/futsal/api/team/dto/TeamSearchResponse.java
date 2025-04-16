package com.swyp.futsal.api.team.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TeamSearchResponse {
    private String id;
    private String name;
    private String leaderName;
    private Integer memberCount;
    private LocalDateTime createdTime;

    public TeamSearchResponse(String id, String name, String leaderName, Integer memberCount, LocalDateTime createdTime) {
        this.id = id;
        this.name = name;
        this.leaderName = leaderName;
        this.memberCount = memberCount;
        this.createdTime = createdTime;
    }
}
