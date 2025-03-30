package com.swyp.futsal.domain.team.entity;

import com.swyp.futsal.domain.common.BaseEntity;
import com.swyp.futsal.domain.common.enums.MemberStatus;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_member")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column
    private String name;

    @Column(name = "squad_number")
    private Integer squadNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;
} 