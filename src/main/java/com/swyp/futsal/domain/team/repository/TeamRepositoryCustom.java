package com.swyp.futsal.domain.team.repository;

import com.swyp.futsal.domain.common.enums.MatchType;
import com.swyp.futsal.domain.common.enums.TeamRole;
import java.util.List;
import com.querydsl.core.Tuple;


public interface TeamRepositoryCustom {
    String insert(String userId, String name, String introduction, String rule, MatchType matchType, TeamRole access, Integer dues);
    List<Tuple> findAllWithLeaderByNameContaining(String name);
    boolean existsTeamByName(String name);
    void updateLogoById(String id, String uri);
}