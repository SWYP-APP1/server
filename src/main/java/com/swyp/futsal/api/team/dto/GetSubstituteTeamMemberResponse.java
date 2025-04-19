package com.swyp.futsal.api.team.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.swyp.futsal.domain.common.enums.TeamRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetSubstituteTeamMemberResponse {
    @Getter
    @Builder
    public static class TeamMember {
        private String id;
        private String name;
        private Optional<String> profileUrl;
        private TeamRole role;
        private LocalDateTime createdTime;
    }

    private List<TeamMember> members;
}
