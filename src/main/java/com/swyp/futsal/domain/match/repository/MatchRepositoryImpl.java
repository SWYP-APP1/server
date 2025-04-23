package com.swyp.futsal.domain.match.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swyp.futsal.domain.common.enums.MatchStatus;
import com.swyp.futsal.domain.common.enums.VoteStatus;
import com.swyp.futsal.domain.match.entity.Match;
import com.swyp.futsal.domain.match.entity.QMatch;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchRepositoryImpl implements MatchRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QMatch match = QMatch.match;

    @Override
    public Page<Match> findAllByTeamIdOrderByMatchDateDesc(String teamId, Pageable pageable) {
        List<Match> matches = queryFactory.selectFrom(match)
            .where(match.team.id.eq(teamId))
            .orderBy(match.matchDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
        return new PageImpl<>(matches, pageable, matches.size());
    }

    @Override
    public List<Match> findAllByTeamId(String teamId) {
        return queryFactory.selectFrom(match)
            .where(match.team.id.eq(teamId))
            .orderBy(match.matchDate.desc())
            .fetch();
    }

    @Override
    public void updateRoundsById(String id, Integer rounds) {
        queryFactory.update(match)
            .set(match.rounds, rounds)
            .where(match.id.eq(id))
            .execute();
    }

    @Override
    public void updateVoteStatusById(String id, VoteStatus voteStatus) {
        queryFactory.update(match)
            .set(match.voteStatus, voteStatus)
            .where(match.id.eq(id))
            .execute();
    }

    @Override
    public void updateStatusById(String id, MatchStatus matchStatus) {
        queryFactory.update(match)
            .set(match.status, matchStatus)
            .where(match.id.eq(id))
            .execute();
    }

    @Override
    public Optional<Match> findFirstRecentMatch(String today, String teamId) {
        return Optional.ofNullable(queryFactory.selectFrom(match)
            .where(match.matchDate.loe(today), match.team.id.eq(teamId))
            .orderBy(match.matchDate.desc())
            .fetchFirst());
    }
}