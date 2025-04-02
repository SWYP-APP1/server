package com.swyp.futsal.domain.team.repository;

import static com.swyp.futsal.domain.team.entity.QTeam.team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swyp.futsal.domain.common.enums.MatchType;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.team.entity.Team;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public String insert(String userId, String name, String introduction, String rule, MatchType matchType, TeamRole access, Integer dues) {
        long result = queryFactory
                .insert(team)
                .values(userId, name, introduction, rule, matchType, access, dues)
                .execute();
        return String.valueOf(result);
    }

    @Override
    public List<Team> findTeamsByNameContaining(String name) {
        return queryFactory
                .selectFrom(team)
                .where(team.name.containsIgnoreCase(name))
                .orderBy(team.name.asc())
                .fetch();
    }

    @Override
    public boolean existsTeamByName(String name) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(team)
                .where(team.name.eq(name))
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public void updateLogoById(String id, String uri) {
        queryFactory
                .update(team)
                .set(team.logoUri, uri)
                .execute();
    }

}