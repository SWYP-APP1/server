package com.swyp.futsal.api.match.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;
import java.util.Map;

@Getter
@Builder
public class MatchStatsListResponse {
    @Getter
    @Builder
    public static class RoundStats {
        @Getter
        @Builder
        public static class GoalAssistPair {
            private MatchStatsResponse goal;
            private MatchStatsResponse assist;
        }

        private List<GoalAssistPair> teamA;
        private List<GoalAssistPair> teamB;
    }

    private Map<Integer, RoundStats> stats;
} 