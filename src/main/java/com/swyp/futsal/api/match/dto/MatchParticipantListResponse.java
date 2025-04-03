package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.common.enums.SubTeam;
import com.swyp.futsal.domain.match.entity.MatchParticipant;
import com.swyp.futsal.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
public class MatchParticipantListResponse {
    private List<Participant> participants;
    private boolean isParticipate;

    @Getter
    @Builder
    public static class Participant {
        private String id;
        private String matchId;
        private String teamMemberId;
        private String name;
        private String profileUrl;
        private SubTeam subTeam;
        private LocalDateTime createdTime;

        public static Participant from(MatchParticipant participant, User user, String profileUrl) {
            return Participant.builder()
                    .id(participant.getId())
                    .matchId(participant.getMatch().getId())
                    .teamMemberId(participant.getTeamMember().getId())
                    .name(user.getName())
                    .profileUrl(profileUrl)
                    .subTeam(participant.getSubTeam())
                    .createdTime(participant.getCreatedTime())
                    .build();
        }
    }

    public static Comparator<Participant> participantComparator() {
        return Comparator
                .comparing(Participant::getSubTeam)
                .thenComparing(Participant::getName);
    }
} 