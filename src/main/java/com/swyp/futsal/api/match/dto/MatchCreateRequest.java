package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.common.annotations.TimeFormat;
import com.swyp.futsal.domain.common.enums.MatchStatus;
import com.swyp.futsal.domain.common.enums.MatchType;
import com.swyp.futsal.domain.common.enums.VoteStatus;
import com.swyp.futsal.domain.match.entity.Match;
import com.swyp.futsal.domain.team.entity.Team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MatchCreateRequest {
    @NotNull
    private String teamId;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    private String matchDate;

    @NotNull
    private MatchType type;

    @NotBlank
    @Size(min = 2)
    private String location;

    @TimeFormat
    private String startTime;

    @TimeFormat
    private String endTime;

    private String opponentTeamName;
    private String substituteTeamMemberId;
    private String description;
    private boolean isVote = false;

    public Match toMatch(Team team) {
        Match match = Match.builder()
        .team(team)
        .matchDate(this.matchDate)
        .startTime(this.startTime)
        .endTime(this.endTime)
        .type(this.type)
        .location(this.location)
        .opponentTeamName(this.opponentTeamName)
        .substituteTeamMemberId(this.substituteTeamMemberId)
        .description(this.description)
        .voteStatus(VoteStatus.NONE)
        .status(MatchStatus.DRAFT)
        .build();
        
        return match;
    }
} 