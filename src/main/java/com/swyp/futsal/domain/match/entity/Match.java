package com.swyp.futsal.domain.match.entity;

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

import java.time.LocalDateTime;

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
    private Integer rounds;

    @Column(name = "match_date", nullable = false)
    private String matchDate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

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
}