package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.common.enums.ParticipationStatus;
import com.swyp.futsal.domain.match.entity.Vote;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class VoteListResponse {
    private List<VoteInfo> votes;

    @Getter
    @Builder
    public static class VoteInfo {
        private String id;
        private String matchId;
        private String voterTeamMemberId;
        private boolean isMine;
        private ParticipationStatus participationChoice;

        public static VoteInfo from(Vote vote, boolean isMine) {
            return VoteInfo.builder()
                    .id(vote.getId())
                    .matchId(vote.getMatch().getId())
                    .voterTeamMemberId(vote.getVoterTeamMember().getId())
                    .isMine(isMine)
                    .participationChoice(vote.getParticipationChoice())
                    .build();
        }
    }
} 