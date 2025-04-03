package com.swyp.futsal.api.match.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchNotePresignedUrlResponse {
    private String url;
    private String uri;
} 