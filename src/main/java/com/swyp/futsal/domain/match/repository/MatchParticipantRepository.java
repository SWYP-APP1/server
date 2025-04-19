package com.swyp.futsal.domain.match.repository;

import com.querydsl.core.Tuple;
import com.swyp.futsal.domain.match.entity.MatchParticipant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, String>, MatchParticipantRepositoryCustom {
    List<MatchParticipant> findAllByIdsAndMatchId(List<String> ids, String matchId);
    List<MatchParticipant> findAllByMatchId(String matchId);
    List<Tuple> findAllWithMatchByTeamMemberId(String teamMemberId);
    @Query("SELECT mp FROM MatchParticipant mp WHERE mp.match.id = :matchId AND mp.teamMember.id != :excludeTeamMemberId")
    List<MatchParticipant> findAllByMatchIdAndTeamMemberIdNot(String matchId, String excludeTeamMemberId);

    @Query("SELECT mp FROM MatchParticipant mp WHERE mp.match.id = :matchId AND mp.teamMember.id = :teamMemberId")
    Optional<MatchParticipant> findByMatchIdAndTeamMemberId(String matchId, String teamMemberId);
    
} 