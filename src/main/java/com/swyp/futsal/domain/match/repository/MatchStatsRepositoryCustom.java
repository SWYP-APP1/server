package com.swyp.futsal.domain.match.repository;

import java.util.List;

import com.swyp.futsal.domain.match.entity.MatchStats;

public interface MatchStatsRepositoryCustom {
    List<MatchStats> findAllByMatchIdOrderByRoundNumberAscHistoryTimeAsc(String matchId);
    boolean existsByAssistedMatchStatId(String matchStatId);
}
