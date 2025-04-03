package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.common.enums.StatType;
import com.swyp.futsal.domain.match.entity.MatchStats;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MatchStatsResponse {
    private String id;
    private String matchParticipantId;
    private Integer roundNumber;
    private StatType statType;
    private String assistedMatchStatId;
    private LocalDateTime historyTime;
    private LocalDateTime createdTime;

    public static MatchStatsResponse from(MatchStats stats) {
        return MatchStatsResponse.builder()
                .id(stats.getId())
                .matchParticipantId(stats.getMatchParticipant().getId())
                .roundNumber(stats.getRoundNumber())
                .statType(stats.getStatType())
                .assistedMatchStatId(stats.getAssistedMatchStatId())
                .historyTime(stats.getHistoryTime())
                .createdTime(stats.getCreatedTime())
                .build();
    }
} 