package com.swyp.futsal.api.match.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MatchStatsListResponse {
    private List<List<MatchStatsResponse>> stats;
} 