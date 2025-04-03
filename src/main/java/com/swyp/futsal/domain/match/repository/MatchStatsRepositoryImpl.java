package com.swyp.futsal.domain.match.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swyp.futsal.domain.match.entity.MatchStats;
import com.swyp.futsal.domain.match.entity.QMatchStats;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchStatsRepositoryImpl implements MatchStatsRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QMatchStats matchStats = QMatchStats.matchStats;

    @Override
    public List<MatchStats> findAllByMatchIdOrderByRoundNumberAscHistoryTimeAsc(String matchId) {
        return queryFactory.selectFrom(matchStats)
                .where(matchStats.match.id.eq(matchId))
                .orderBy(matchStats.roundNumber.asc(), matchStats.historyTime.asc())
                .fetch();
    }

    @Override
    public boolean existsByAssistedMatchStatId(String matchStatId) {
        return queryFactory.selectOne()
                .from(matchStats)
                .where(matchStats.assistedMatchStatId.eq(matchStatId))
                .fetchFirst() != null;
    }
}
