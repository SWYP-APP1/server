package com.swyp.futsal.domain.match.repository;

import com.swyp.futsal.domain.common.enums.VoteType;
import com.swyp.futsal.domain.match.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, String> {
    @Query("SELECT v FROM Vote v WHERE v.match.id = :matchId AND v.voteType = :voteType ORDER BY v.createdTime DESC")
    List<Vote> findAllByMatchIdAndVoteTypeOrderByCreatedTimeDesc(String matchId, VoteType voteType);
    
    @Query("SELECT v FROM Vote v WHERE v.match.id = :matchId AND v.voterTeamMember.id = :voterTeamMemberId AND v.voteType = :voteType")
    Optional<Vote> findByMatchIdAndVoterTeamMemberIdAndVoteType(String matchId, String voterTeamMemberId, VoteType voteType);
} 