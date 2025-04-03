package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.match.entity.MatchParticipant;
import com.swyp.futsal.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MatchParticipantMomResponse {
    private String matchId;
    private List<Participant> participants;

    @Getter
    @Builder
    public static class Participant {
        private String id;
        private String name;

        public static Participant from(MatchParticipant participant, User user) {
            return Participant.builder()
                    .id(participant.getId())
                    .name(user.getName())
                    .build();
        }
    }
} 