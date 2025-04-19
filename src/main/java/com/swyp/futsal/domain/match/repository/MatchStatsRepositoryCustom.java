package com.swyp.futsal.domain.match.repository;

import java.util.List;

import com.querydsl.core.Tuple;
import com.swyp.futsal.domain.common.enums.StatType;
import com.swyp.futsal.domain.match.entity.MatchStats;

public interface MatchStatsRepositoryCustom {
    List<MatchStats> findAllByMatchIdOrderByRoundNumberAscHistoryTimeAsc(String matchId);
    List<Tuple> findAllWithMatchParticipantByMatchIdAndStatType(String matchId, StatType statType);
    boolean existsByAssistedMatchStatId(String matchStatId);
}
