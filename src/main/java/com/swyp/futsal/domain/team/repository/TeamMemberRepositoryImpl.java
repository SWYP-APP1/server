package com.swyp.futsal.domain.team.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swyp.futsal.domain.team.entity.TeamMember;
import com.swyp.futsal.domain.team.entity.QTeamMember;

import lombok.RequiredArgsConstructor;

import static com.swyp.futsal.domain.team.entity.QTeam.team;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TeamMemberRepositoryImpl implements TeamMemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QTeamMember teamMember = QTeamMember.teamMember;

    @Override
    public Optional<Tuple> findOneWithTeamByUserAndIsDeletedFalse(String userId) {
        return Optional.ofNullable(queryFactory
                .select(teamMember, team)
                .from(teamMember)
                .join(teamMember.team, team)
                .where(teamMember.user.id.eq(userId))
                .fetchOne());
    }

    @Override
    public Optional<TeamMember> findByUserAndTeamAndIsDeletedFalse(String userId, String teamId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(teamMember)
                .where(teamMember.user.id.eq(userId), teamMember.team.id.eq(teamId))
                .fetchOne());
    }

    @Override
    public boolean existsByUserAndTeamAndIsDeletedFalse(String userId, String teamId) {
        return queryFactory
                .selectFrom(teamMember)
                .where(teamMember.user.id.eq(userId), teamMember.team.id.eq(teamId))
                .fetchOne() != null;
    }

    @Override   
    public List<TeamMember> findTeamMembersByTeamId(String teamId) {
        return queryFactory
                .selectFrom(teamMember)
                .where(teamMember.team.id.eq(teamId))
                .fetch();
    }
}