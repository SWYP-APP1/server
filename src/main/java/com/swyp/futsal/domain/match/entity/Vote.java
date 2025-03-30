package com.swyp.futsal.domain.match.entity;

import com.swyp.futsal.domain.common.BaseEntity;
import com.swyp.futsal.domain.common.enums.ParticipationStatus;
import com.swyp.futsal.domain.common.enums.VoteType;
import com.swyp.futsal.domain.team.entity.TeamMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vote")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_team_member_id", nullable = false)
    private TeamMember voterTeamMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_team_member_id")
    private TeamMember targetTeamMember;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false)
    private VoteType voteType;

    @Enumerated(EnumType.STRING)
    @Column(name = "participation_choice")
    private ParticipationStatus participationChoice;
} 