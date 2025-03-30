package com.swyp.futsal.domain.team.repository;

import com.swyp.futsal.domain.team.entity.Team;
import java.util.List;

public interface TeamRepositoryCustom {
    List<Team> findTeamsByNameContaining(String name);

    boolean existsTeamByName(String name);

    void updateLogoById(String id, String uri);
}