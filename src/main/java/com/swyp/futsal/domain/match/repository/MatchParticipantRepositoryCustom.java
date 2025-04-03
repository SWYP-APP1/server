package com.swyp.futsal.domain.match.repository;

import java.util.List;

import com.swyp.futsal.domain.match.entity.MatchParticipant;

public interface MatchParticipantRepositoryCustom {
    List<MatchParticipant> findAllByIdsAndMatchId(List<String> ids, String matchId);
    List<MatchParticipant> findAllByMatchId(String matchId);
}
