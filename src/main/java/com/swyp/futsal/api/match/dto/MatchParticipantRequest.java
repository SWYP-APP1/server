package com.swyp.futsal.api.match.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MatchParticipantRequest {
    private String matchId;
    private List<String> teamMemberIds;
}