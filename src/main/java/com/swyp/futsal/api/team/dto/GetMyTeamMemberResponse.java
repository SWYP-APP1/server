package com.swyp.futsal.api.team.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMyTeamMemberResponse {
    @Getter
    public static class TeamInfo {
        private String id;
        private String name;
        private String role;

        public TeamInfo(String id, String name, String role) {
            this.id = id;
            this.name = name;
            this.role = role;
        }
    }

    @Getter
    public static class MatchInfo {
        @Getter
        public static class MatchHistory {
            private String id;
            private String result;

            public MatchHistory(String id, String result) {
                this.id = id;
                this.result = result;
            }
        }

        private Integer total;
        private List<MatchHistory> history;

        public MatchInfo(Integer total, List<MatchHistory> history) {
            this.total = total;
            this.history = history;
        }
    }

    private String id;
    private String name;
    private Optional<String> birthDate;
    private Optional<String> generation;
    private Integer squadNumber;
    private Optional<String> profileUrl;
    private LocalDateTime createdTime;
    private TeamInfo team;
    private MatchInfo match;
}
