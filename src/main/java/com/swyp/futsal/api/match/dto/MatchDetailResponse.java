package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.common.enums.MatchType;
import com.swyp.futsal.domain.match.entity.Match;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MatchDetailResponse {
    private String id;
    private MatchType type;
    private String matchDate;
    private String startTime;
    private String endTime;
    private String location;
    private String opponentTeamName;
    private Mom mom;
    private LocalDateTime createdTime;

    @Getter
    @Builder
    public static class Mom {
        private String profileUrl;
        private String name;
    }

    public static MatchDetailResponse from(Match match) {
        MatchDetailResponse.Mom mom = MatchDetailResponse.Mom.builder()
                .profileUrl(null)
                .name(null)
                .build();
        return MatchDetailResponse.builder()
                .id(match.getId())
                .type(match.getType())
                .matchDate(match.getMatchDate())
                .startTime(match.getStartTime())
                .endTime(match.getEndTime())
                .location(match.getLocation())
                .opponentTeamName(match.getOpponentTeamName())
                .mom(mom)
                .createdTime(match.getCreatedTime())
                .build();
    }
} 