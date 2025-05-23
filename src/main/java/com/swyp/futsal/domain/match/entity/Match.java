package com.swyp.futsal.domain.match.entity;

import java.util.Optional;

import com.swyp.futsal.domain.common.BaseEntity;
import com.swyp.futsal.domain.common.enums.MatchStatus;
import com.swyp.futsal.domain.common.enums.MatchType;
import com.swyp.futsal.domain.common.enums.VoteStatus;
import com.swyp.futsal.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "matches")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Match extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column
    private String description;

    @Column(name = "opponent_team_name")
    private String opponentTeamName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchType type;

    @Column(nullable = false)
    @Builder.Default
    private Integer rounds = 3;

    @Column(name = "match_date", nullable = false)
    private String matchDate;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(nullable = false)
    private String location;

    @Column(name = "substitute_team_member_id")
    private String substituteTeamMemberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_status", nullable = false)
    private VoteStatus voteStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    public void updateRounds(Integer rounds) {
        this.rounds = rounds;
    }

    public void updateMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public void updateStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void updateEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateSubstituteTeamMemberId(Optional<String> substituteTeamMemberId) {
        this.substituteTeamMemberId = substituteTeamMemberId.orElse(null);
    }

    public void updateVoteStatusToEnded() {
        this.voteStatus = VoteStatus.ENDED;
    }

    public void updateStatusToOngoing() {
        this.status = MatchStatus.ONGOING;
    }
}