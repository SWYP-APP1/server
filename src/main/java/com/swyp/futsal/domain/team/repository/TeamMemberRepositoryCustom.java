package com.swyp.futsal.domain.team.repository;

import com.swyp.futsal.domain.team.entity.TeamMember;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.Tuple;

public interface TeamMemberRepositoryCustom {
    Optional<Tuple> findOneWithTeamByUserAndIsDeletedFalse(String userId);
    Optional<Tuple> findOneWithTeamByUserAndTeamIdAndIsDeletedFalse(String userId, String teamId);
    Optional<TeamMember> findByUserAndTeamAndIsDeletedFalse(String userId, String teamId);
    boolean existsByUserAndTeamAndIsDeletedFalse(String userId, String teamId);
    List<TeamMember> findTeamMembersByTeamId(String teamId);
    List<TeamMember> findTeamMembersByTeamIdAndMemberIds(String teamId, List<String> memberIds);
}
