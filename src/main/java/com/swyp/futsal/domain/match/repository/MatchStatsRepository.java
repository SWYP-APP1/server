package com.swyp.futsal.domain.match.repository;

import com.swyp.futsal.domain.match.entity.MatchStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchStatsRepository extends JpaRepository<MatchStats, String> {
} 