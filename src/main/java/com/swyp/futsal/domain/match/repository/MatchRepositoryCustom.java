package com.swyp.futsal.domain.match.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swyp.futsal.domain.common.enums.MatchStatus;
import com.swyp.futsal.domain.common.enums.VoteStatus;
import com.swyp.futsal.domain.match.entity.Match;



public interface MatchRepositoryCustom {
    Page<Match> findAllByTeamIdOrderByMatchDateDesc(String teamId, Pageable pageable);
    List<Match> findAllByTeamId(String teamId);
    void updateRoundsById(String id, Integer rounds);
    void updateVoteStatusById(String id, VoteStatus voteStatus);
    void updateStatusById(String id, MatchStatus matchStatus);
    Optional<Match> findFirstRecentMatch(String today, String teamId);
}
