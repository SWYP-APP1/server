package com.swyp.futsal.api.match.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VoteMomRequest {
    @NotNull(message = "matchId must not be null")
    private String matchId;
    
    @NotNull(message = "targetMatchParticipantId must not be null")
    private String targetMatchParticipantId;
} 