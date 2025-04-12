package com.swyp.futsal.domain.team.repository;

import com.swyp.futsal.domain.common.enums.MatchType;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.querydsl.core.Tuple;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, String>, TeamRepositoryCustom {
    String insert(String userId, String name, String introduction, String rule, MatchType matchType, TeamRole access, Integer dues);
    boolean existsByName(String name);
    List<Tuple> findAllWithLeaderByNameContaining(String name);
    void updateLogoById(String id, String uri);
}