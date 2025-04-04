package com.swyp.futsal.domain.match.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swyp.futsal.domain.common.enums.MatchStatus;
import com.swyp.futsal.domain.common.enums.VoteStatus;
import com.swyp.futsal.domain.match.entity.Match;



public interface MatchRepositoryCustom {
    Page<Match> findAllByTeamIdOrderByMatchDateDesc(String teamId, Pageable pageable);
    void updateRoundsById(String id, Integer rounds);
    void updateVoteStatusById(String id, VoteStatus voteStatus);
    void updateStatusById(String id, MatchStatus matchStatus);
}
