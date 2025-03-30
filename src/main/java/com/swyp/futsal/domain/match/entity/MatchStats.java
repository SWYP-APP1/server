package com.swyp.futsal.domain.match.entity;

import com.swyp.futsal.domain.common.BaseEntity;
import com.swyp.futsal.domain.common.enums.StatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_stats")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchStats extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_participant_id", nullable = false)
    private MatchParticipant matchParticipant;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "stat_type", nullable = false)
    private StatType statType;

    @Column(name = "assisted_match_stat_id")
    private String assistedMatchStatId;

    @Column(name = "history_time", nullable = false)
    private LocalDateTime historyTime;
} 