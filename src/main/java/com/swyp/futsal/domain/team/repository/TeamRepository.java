package com.swyp.futsal.domain.team.repository;

import com.swyp.futsal.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, String>, TeamRepositoryCustom {
    boolean existsByName(String name);

    List<Team> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    void updateLogoById(String id, String uri);
}