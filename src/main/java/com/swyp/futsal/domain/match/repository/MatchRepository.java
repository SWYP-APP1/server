package com.swyp.futsal.domain.match.repository;

import com.swyp.futsal.domain.common.enums.MatchStatus;
import com.swyp.futsal.domain.common.enums.VoteStatus;
import com.swyp.futsal.domain.match.entity.Match;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, String>, MatchRepositoryCustom {
    Page<Match> findAllByTeamIdOrderByMatchDateDesc(String teamId, Pageable pageable);
    void updateRoundsById(String id, Integer rounds);
    void updateVoteStatusById(String id, VoteStatus voteStatus);
    void updateStatusById(String id, MatchStatus matchStatus);

    @Query("SELECT m FROM Match m WHERE m.matchDate LIKE :yearMonth% ORDER BY m.matchDate DESC")
    List<Match> findAllByMatchDateStartingWith(String yearMonth);
} 