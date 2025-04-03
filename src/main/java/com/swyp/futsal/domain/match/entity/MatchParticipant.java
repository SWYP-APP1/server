package com.swyp.futsal.domain.match.entity;

import com.swyp.futsal.domain.common.BaseEntity;
import com.swyp.futsal.domain.common.enums.ParticipationStatus;
import com.swyp.futsal.domain.common.enums.SubTeam;
import com.swyp.futsal.domain.team.entity.TeamMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_participant")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_member_id", nullable = false)
    private TeamMember teamMember;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_team", nullable = false)
    private SubTeam subTeam;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "position")
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status;

    @Builder
    public MatchParticipant(String id, Match match, TeamMember teamMember, SubTeam subTeam, String position, ParticipationStatus status) {
        this.id = id;
        this.match = match;
        this.teamMember = teamMember;
        this.subTeam = subTeam;
        this.position = position;
        this.status = status;
    }

    public void updateSubTeam(SubTeam subTeam) {
        this.subTeam = subTeam;
    }
} 