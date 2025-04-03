package com.swyp.futsal.domain.match.repository;

import java.util.Optional;

import com.swyp.futsal.domain.match.entity.MatchNote;

public interface MatchNoteRepositoryCustom {
    Optional<MatchNote> findOneByMatchId(String matchId);
    boolean existsByMatchIdAndUserId(String matchId, String userId);
}
