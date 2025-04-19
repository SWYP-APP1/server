package com.swyp.futsal.domain.team.repository;

import com.swyp.futsal.domain.common.enums.MemberStatus;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.Tuple;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, String>, TeamMemberRepositoryCustom {
    Optional<Tuple> findOneWithTeamByUserAndIsDeletedFalse(String userId);
    Optional<Tuple> findOneWithTeamByUserAndTeamIdAndIsDeletedFalse(String userId, String teamId);
    Optional<Tuple> findOneWithTeamByTeamMemberIdAndIsDeletedFalse(String teamMemberId);
    Optional<TeamMember> findByUserAndTeamAndIsDeletedFalse(String userId, String teamId);
    boolean existsByUserAndTeamAndIsDeletedFalse(String userId, String teamId);
    List<TeamMember> findTeamMembersByTeamId(String teamId);
    List<Tuple> findTeamMembersInfoByTeamId(String teamId);
    List<TeamMember> findTeamMembersByTeamIdAndMemberIds(String teamId, List<String> memberIds);
    void updateMemberStatus(String teamId, String userId, MemberStatus memberStatus);
    void updateRoleTeamMember(String teamId, String userId, TeamRole role);
    List<Tuple> countWithTeamIdByTeamIds(List<String> teamIds);
}