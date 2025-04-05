package com.swyp.futsal.domain.match.service;

import com.swyp.futsal.api.match.dto.*;
import com.swyp.futsal.domain.common.enums.VoteStatus;
import com.swyp.futsal.domain.common.enums.VoteType;
import com.swyp.futsal.domain.match.entity.Match;
import com.swyp.futsal.domain.match.entity.MatchParticipant;
import com.swyp.futsal.domain.match.entity.Vote;
import com.swyp.futsal.domain.match.repository.MatchParticipantRepository;
import com.swyp.futsal.domain.match.repository.MatchRepository;
import com.swyp.futsal.domain.match.repository.VoteRepository;
import com.swyp.futsal.domain.team.entity.TeamMember;
import com.swyp.futsal.domain.team.repository.TeamMemberRepository;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {
    private final Logger logger = LoggerFactory.getLogger(VoteService.class);
    private final VoteRepository voteRepository;
    private final MatchRepository matchRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    @Transactional(readOnly = true)
    public VoteListResponse getVotes(String userId, String matchId, VoteType type) {
        logger.info("Get votes: userId={}, matchId={}, type={}", userId, matchId, type);
        
        TeamMember voter = validateTeamMember(userId, matchId);
        List<Vote> votes = voteRepository.findAllByMatchIdAndVoteTypeOrderByCreatedTimeDesc(matchId, type);
        
        List<VoteListResponse.VoteInfo> voteInfos = votes.stream()
                .map(vote -> VoteListResponse.VoteInfo.from(
                        vote, 
                        vote.getVoterTeamMember().getId().equals(voter.getId())
                ))
                .collect(Collectors.toList());

        return VoteListResponse.builder()
                .votes(voteInfos)
                .build();
    }

    @Transactional
    public void voteMom(String userId, VoteMomRequest request) {
        logger.info("Vote MOM: userId={}, request={}", userId, request);
        
        logger.info("Validate team member: userId={}, matchId={}", userId, request.getMatchId());
        TeamMember voter = validateTeamMember(userId, request.getMatchId());

        logger.info("Validate team member: userId={}, matchId={}", userId, request.getMatchId());
        checkIfTeamMemberParticipantedInMatch(voter.getId(), request.getMatchId());
        MatchParticipant targetParticipant = matchParticipantRepository.findById(request.getTargetMatchParticipantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_PARTICIPANT_ID));

        // Check if already voted
        logger.info("Check if already voted: matchId={}, voterId={}, voteType={}", request.getMatchId(), voter.getId(), VoteType.MOM);
        voteRepository.findByMatchIdAndVoterTeamMemberIdAndVoteType(request.getMatchId(), voter.getId(), VoteType.MOM)
                .ifPresent(v -> {
                    throw new BusinessException(ErrorCode.CONFLICT_ALREADY_VOTED);
                });

        logger.info("Create vote: matchId={}, voterId={}, targetId={}, voteType={}", request.getMatchId(), voter.getId(), targetParticipant.getTeamMember().getId(), VoteType.MOM);
        voteRepository.save(Vote.builder()
                .match(targetParticipant.getMatch())
                .voterTeamMember(voter)
                .targetTeamMember(targetParticipant.getTeamMember())
                .voteType(VoteType.MOM)
                .build());
    }

    @Transactional
    public void voteParticipation(String userId, VoteParticipationRequest request) {
        logger.info("Vote participation: userId={}, request={}", userId, request);
        
        TeamMember voter = validateTeamMember(userId, request.getMatchId());
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));
        
        if (match.getVoteStatus() == VoteStatus.NONE) {
            matchRepository.updateVoteStatusById(request.getMatchId(), VoteStatus.REGISTERED);
        } else if (match.getVoteStatus() == VoteStatus.ENDED) {
            throw new BusinessException(ErrorCode.CONFLICT_VOTE_STATUS_ENDED);
        }

        // Check if already voted and update if exists
        voteRepository.findByMatchIdAndVoterTeamMemberIdAndVoteType(request.getMatchId(), voter.getId(), VoteType.PARTICIPATION)
                .ifPresentOrElse(
                    existingVote -> {
                        logger.info("Updating existing vote: voteId={}, newChoice={}", existingVote.getId(), request.getParticipationChoice());
                        existingVote.updateParticipationChoice(request.getParticipationChoice());
                        voteRepository.save(existingVote);
                    },
                    () -> {
                        logger.info("Creating new vote: matchId={}, voterId={}", request.getMatchId(), voter.getId());
                        Vote newVote = Vote.builder()
                                .match(match)
                                .voterTeamMember(voter)
                                .voteType(VoteType.PARTICIPATION)
                                .participationChoice(request.getParticipationChoice())
                                .build();
                        voteRepository.save(newVote);
                    }
                );
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getVoteSchedule(String userId, String date) {
        logger.info("Get vote schedule: userId={}, date={}", userId, date);
        return matchRepository.findAllByMatchDateStartingWith(date)
                .stream()
                .map(MatchResponse::from)
                .collect(Collectors.toList());
    }

    private MatchParticipant checkIfTeamMemberParticipantedInMatch(String teamMemberId, String matchId) {
        MatchParticipant matchParticipant = matchParticipantRepository.findByMatchIdAndTeamMemberId(matchId, teamMemberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED));

        return matchParticipant;
    }

    private TeamMember validateTeamMember(String userId, String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));

        return teamMemberRepository.findByUserAndTeamAndIsDeletedFalse(userId, match.getTeam().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED));
    }
} 