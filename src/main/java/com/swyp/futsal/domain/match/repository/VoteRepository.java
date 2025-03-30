package com.swyp.futsal.domain.match.repository;

import com.swyp.futsal.domain.match.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, String> {
} 