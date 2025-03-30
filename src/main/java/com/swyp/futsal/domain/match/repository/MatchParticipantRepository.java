package com.swyp.futsal.domain.match.repository;

import com.swyp.futsal.domain.match.entity.MatchParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, String> {
} 