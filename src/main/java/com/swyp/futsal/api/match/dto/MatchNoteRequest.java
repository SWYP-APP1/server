package com.swyp.futsal.api.match.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.util.List;

@Getter
public class MatchNoteRequest {
    private String id;
    
    @NotNull(message = "matchId must not be null")
    private String matchId;
    
    private String description;
    private List<String> photoUris;
} 