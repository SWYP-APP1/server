package com.swyp.futsal.domain.match.repository;

import com.querydsl.core.Tuple;
import com.swyp.futsal.domain.common.enums.StatType;
import com.swyp.futsal.domain.match.entity.MatchStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchStatsRepository extends JpaRepository<MatchStats, String>, MatchStatsRepositoryCustom {
    List<MatchStats> findAllByMatchIdOrderByRoundNumberAscHistoryTimeAsc(String matchId);
    List<Tuple> findAllWithMatchParticipantByMatchIdAndStatType(String matchId, StatType statType);
    boolean existsByAssistedMatchStatId(String matchStatId);
} 