package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.match.entity.Match;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MatchResponse {
    private String id;
    private String substituteTeamMemberId;
    private String opponentTeamName;
    private String description;
    private Integer rounds;
    private String type;
    private String matchDate;
    private String startTime;
    private String endTime;
    private String location;
    private String voteStatus;
    private String status;
    private LocalDateTime createdTime;

    public static MatchResponse from(Match match) {
        MatchResponse response = new MatchResponse();
        response.setId(match.getId());
        response.setSubstituteTeamMemberId(match.getSubstituteTeamMemberId());
        response.setOpponentTeamName(match.getOpponentTeamName());
        response.setDescription(match.getDescription());
        response.setType(match.getType() != null ? match.getType().name() : null);
        response.setRounds(match.getRounds());
        response.setMatchDate(match.getMatchDate());
        response.setStartTime(match.getStartTime());
        response.setEndTime(match.getEndTime());
        response.setLocation(match.getLocation());
        response.setVoteStatus(match.getVoteStatus() != null ? match.getVoteStatus().name() : null);
        response.setStatus(match.getStatus() != null ? match.getStatus().name() : null);
        response.setCreatedTime(match.getCreatedTime());
        return response;
    }
} 