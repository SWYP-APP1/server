package com.swyp.futsal.domain.match.repository;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swyp.futsal.domain.match.entity.MatchNote;
import com.swyp.futsal.domain.match.entity.QMatchNote;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchNoteRepositoryImpl implements MatchNoteRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QMatchNote matchNote = QMatchNote.matchNote;

    @Override
    public Optional<MatchNote> findOneByMatchId(String matchId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(matchNote)
                .where(matchNote.match.id.eq(matchId))
                .fetchOne());
    }

    @Override
    public boolean existsByMatchIdAndUserId(String matchId, String userId) {
        return queryFactory
                .selectFrom(matchNote)
                .where(matchNote.match.id.eq(matchId)
                        .and(matchNote.user.id.eq(userId)))
                .fetchFirst() != null;
    }
}