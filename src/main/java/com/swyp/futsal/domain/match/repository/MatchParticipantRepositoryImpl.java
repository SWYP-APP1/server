package com.swyp.futsal.domain.match.repository;

import java.util.List;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swyp.futsal.domain.match.entity.MatchParticipant;
import com.swyp.futsal.domain.match.entity.QMatch;
import com.swyp.futsal.domain.match.entity.QMatchParticipant;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchParticipantRepositoryImpl implements MatchParticipantRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QMatchParticipant matchParticipant = QMatchParticipant.matchParticipant;
    private final QMatch match = QMatch.match;

    @Override
    public List<MatchParticipant> findAllByIdsAndMatchId(List<String> ids, String matchId) {
        return queryFactory.selectFrom(matchParticipant)
                .where(matchParticipant.id.in(ids), matchParticipant.match.id.eq(matchId))
                .fetch();
    }

    @Override
    public List<MatchParticipant> findAllByMatchId(String matchId) {
        return queryFactory.selectFrom(matchParticipant)
                .where(matchParticipant.match.id.eq(matchId))
                .fetch();
    }   

    @Override
    public List<Tuple> findAllWithMatchByTeamMemberId(String teamMemberId) {
        return queryFactory.select(matchParticipant, match)
                .from(matchParticipant)
                .join(matchParticipant.match, match)
                .where(matchParticipant.teamMember.id.eq(teamMemberId))
                .fetch();
    }
    
}
