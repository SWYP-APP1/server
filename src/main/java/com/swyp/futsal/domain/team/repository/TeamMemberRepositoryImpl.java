package com.swyp.futsal.domain.team.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swyp.futsal.domain.common.enums.MemberStatus;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.team.entity.TeamMember;
import com.swyp.futsal.domain.team.entity.QTeamMember;

import com.swyp.futsal.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;

import static com.swyp.futsal.domain.team.entity.QTeam.team;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TeamMemberRepositoryImpl implements TeamMemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QTeamMember teamMember = QTeamMember.teamMember;
    private final QUser user = QUser.user;

    @Override
    public Optional<Tuple> findOneWithTeamByUserAndIsDeletedFalse(String userId) {
        return Optional.ofNullable(queryFactory
                .select(teamMember, team)
                .from(teamMember)
                .join(teamMember.team, team)
                .where(teamMember.user.id.eq(userId), teamMember.isDeleted.eq(false))
                .fetchOne());
    }

    @Override
    public Optional<Tuple> findOneWithTeamByUserAndTeamIdAndIsDeletedFalse(String userId, String teamId) {
        return Optional.ofNullable(queryFactory
                .select(teamMember, team)
                .from(teamMember)
                .join(teamMember.team, team)
                .where(teamMember.user.id.eq(userId), teamMember.team.id.eq(teamId), teamMember.isDeleted.eq(false))
                .fetchOne());
    }

    @Override
    public Optional<Tuple> findOneWithTeamByTeamMemberIdAndIsDeletedFalse(String teamMemberId) {
        return Optional.ofNullable(queryFactory
                .select(teamMember, team)
                .from(teamMember)
                .join(teamMember.team, team)
                .where(teamMember.id.eq(teamMemberId), teamMember.isDeleted.eq(false))
                .fetchOne());
    }

    @Override
    public Optional<TeamMember> findByUserAndTeamAndIsDeletedFalse(String userId, String teamId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(teamMember)
                .where(teamMember.user.id.eq(userId), teamMember.team.id.eq(teamId), teamMember.isDeleted.eq(false))
                .fetchOne());
    }

    @Override
    public Optional<TeamMember> findOneByTeamIdAndRole(String teamId, TeamRole role) {
        return Optional.ofNullable(queryFactory
                .selectFrom(teamMember)
                .where(teamMember.team.id.eq(teamId), teamMember.role.eq(role), teamMember.isDeleted.eq(false))
                .fetchOne());
    }

    @Override
    public boolean existsByUserAndTeamAndIsDeletedFalse(String userId, String teamId) {
        return queryFactory
                .selectFrom(teamMember)
                .where(teamMember.user.id.eq(userId), teamMember.team.id.eq(teamId), teamMember.isDeleted.eq(false))
                .fetchOne() != null;
    }

    @Override
    public List<Tuple> findAllWithUserByTeamId(String teamId) {
        return queryFactory
                .select(teamMember, user)
                .from(teamMember)
                .join(teamMember.user, user)
                .where(teamMember.team.id.eq(teamId), teamMember.isDeleted.eq(false))
                .fetch();
    }
    
    @Override   
    public List<TeamMember> findTeamMembersByTeamId(String teamId) {
        return queryFactory
                .selectFrom(teamMember)
                .where(teamMember.team.id.eq(teamId), teamMember.isDeleted.eq(false))
                .fetch();
    }

    @Override
    public List<Tuple> findTeamMembersInfoByTeamId(String teamId) {
        return queryFactory
            .select(teamMember, team, user)
            .from(teamMember)
            .join(teamMember.team, team)
            .join(teamMember.user, user)
            .where(teamMember.team.id.eq(teamId), teamMember.isDeleted.eq(false))
            .fetch();
    }

    @Override
    public List<TeamMember> findTeamMembersByTeamIdAndMemberIds(String teamId, List<String> memberIds) {
        return queryFactory
                .selectFrom(teamMember)
                .where(teamMember.team.id.eq(teamId), teamMember.isDeleted.eq(false), teamMember.id.in(memberIds))
                .fetch();
    }

    @Override
    public void updateStatusByIdAndRole(String id, TeamRole role, MemberStatus memberStatus) {
        queryFactory
            .update(teamMember)
            .set(teamMember.status, memberStatus)
            .where(teamMember.id.eq(id), teamMember.role.eq(role))
            .execute();
    }

    @Override
    public void updateRoleById(String id, TeamRole role) {
        queryFactory
            .update(teamMember)
            .set(teamMember.role, role)
            .where(teamMember.id.eq(id))
            .execute();
    }

    @Override
    public void updateSquadNumberById(String id, Integer squadNumber) {
        queryFactory
            .update(teamMember)
            .set(teamMember.squadNumber, squadNumber)
            .where(teamMember.id.eq(id))
            .execute();
    }

    @Override
    public TeamMember getTeamLeaderByTeamMember(String teamId) {
        return queryFactory
            .selectFrom(teamMember)
            .where(teamMember.role.eq(TeamRole.TEAM_LEADER), teamMember.team.id.eq(teamId))
            .fetchOne();
    }

    @Override
    public List<Tuple> countWithTeamIdByTeamIds(List<String> teamIds) {
        return queryFactory
                .select(teamMember.team.id, teamMember.count())
                .from(teamMember)
                .where(teamMember.team.id.in(teamIds))
                .groupBy(teamMember.team.id)
                .fetch();
    }
}