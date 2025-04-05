package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.common.enums.ParticipationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VoteParticipationRequest {
    @NotNull(message = "matchId must not be null")
    private String matchId;
    
    @NotNull(message = "participationChoice must not be null")
    private ParticipationStatus participationChoice;
} 