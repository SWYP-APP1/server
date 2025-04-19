package com.swyp.futsal.api.team.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.swyp.futsal.domain.common.enums.Gender;
import com.swyp.futsal.domain.common.enums.MemberStatus;
import com.swyp.futsal.domain.common.enums.TeamRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetAllTeamMemberResponse {
    @Getter
    @Builder
    public static class TeamMemberInfo {
        private String id;
        private String name;
        private TeamRole role;
        private Optional<String> birthDate;
        private Optional<String> generation;
        private Optional<String> profileUrl;
        private Gender gender;
        private MemberStatus status;
        private LocalDateTime createdTime;
    }

    private String teamId;
    private Optional<String> logoUrl;
    private List<TeamMemberInfo> teamMembers;
}
