package com.swyp.futsal.domain.team.entity;

import com.swyp.futsal.domain.common.BaseEntity;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.common.enums.MatchType;
import com.swyp.futsal.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column
    private String introduction;

    @Column
    private String rule;

    @Column(name = "logo_uri")
    private String logoUri;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole access;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "match_type")
    private MatchType matchType;

    @Column
    private Integer dues;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public void updateLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }
} 