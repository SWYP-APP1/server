package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.common.enums.StatType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MatchStatsCreateRequest {
    @NotNull(message = "matchParticipantId must not be null")
    private String matchParticipantId;
    
    @NotNull(message = "roundNumber must not be null")
    private Integer roundNumber;
    
    @NotNull(message = "statType must not be null")
    private StatType statType;
    
    private String assistedMatchStatId;
} 