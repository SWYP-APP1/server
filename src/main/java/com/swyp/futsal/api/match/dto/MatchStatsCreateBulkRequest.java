package com.swyp.futsal.api.match.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

import com.swyp.futsal.domain.common.enums.SubTeam;

import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@Getter
@Builder
public class MatchStatsCreateBulkRequest {
    @Getter
    @Builder
    public static class MatchStatsCreateRequest {
        @NotNull
        private Integer roundNumber;

        @NotNull
        @Enumerated(EnumType.STRING)
        private SubTeam subTeam;

        @NotNull
        @Length(min = 36, max = 36)
        private String goalMatchParticipantId;
        private Optional<String> assistMatchParticipantId;
    }

    @NotNull
    @Length(min = 36, max = 36)
    private String matchId;

    @Default
    private List<MatchStatsCreateRequest> stats = new ArrayList<>();
}
