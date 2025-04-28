package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.common.enums.SubTeam;
import com.swyp.futsal.domain.match.entity.MatchParticipant;
import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMatchParticipantResponse {
    private List<Participant> participants;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Participant {
        private String id;
        private String matchId;
        private String teamMemberId;
        private String name;
        private String role;
        private String profileUrl;
        private SubTeam subTeam;
        private LocalDateTime createdTime;

        public static Participant from(String name, String profileUrl, MatchParticipant participant) {
            return Participant.builder()
                    .id(participant.getId())
                    .matchId(participant.getMatch().getId())
                    .teamMemberId(participant.getTeamMember().getId())
                    .name(name)
                    .role(participant.getTeamMember().getRole().name())
                    .profileUrl(profileUrl)
                    .subTeam(participant.getSubTeam())
                    .createdTime(participant.getCreatedTime())
                    .build();
        }
    }
} 