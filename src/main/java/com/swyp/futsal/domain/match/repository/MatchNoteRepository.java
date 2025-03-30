package com.swyp.futsal.domain.match.repository;

import com.swyp.futsal.domain.match.entity.MatchNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchNoteRepository extends JpaRepository<MatchNote, String> {
} 