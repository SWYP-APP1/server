package com.swyp.futsal.domain.match.repository;

import com.swyp.futsal.domain.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, String> {
} 