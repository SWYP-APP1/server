package com.swyp.futsal.domain.match.service;

import com.querydsl.core.Tuple;
import com.swyp.futsal.api.match.dto.*;
import com.swyp.futsal.domain.common.enums.StatType;
import com.swyp.futsal.domain.match.entity.Match;
import com.swyp.futsal.domain.match.entity.MatchParticipant;
import com.swyp.futsal.domain.match.entity.MatchStats;
import com.swyp.futsal.domain.match.repository.MatchParticipantRepository;
import com.swyp.futsal.domain.match.repository.MatchRepository;
import com.swyp.futsal.domain.match.repository.MatchStatsRepository;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.entity.TeamMember;
import com.swyp.futsal.domain.team.repository.TeamMemberRepository;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import com.swyp.futsal.util.service.AccessUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchStatsService {
    private final Logger logger = LoggerFactory.getLogger(MatchStatsService.class);
    private final MatchStatsRepository matchStatsRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final MatchRepository matchRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional(readOnly = true)
    public MatchStatsListResponse getMatchStats(String userId, String matchId) {
        logger.info("Get match stats: userId={}, matchId={}", userId, matchId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));

        logger.info("Validate team member role: userId={}, matchId={}", userId, matchId);
        teamMemberRepository.findByUserAndTeamAndIsDeletedFalse(userId, match.getTeam().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED));

        logger.info("Get match stats: userId={}, matchId={}", userId, matchId);
        List<MatchStats> stats = matchStatsRepository
                .findAllByMatchIdOrderByRoundNumberAscHistoryTimeAsc(matchId);

        Map<Integer, List<MatchStats>> statsByRound = stats.stream()
                .collect(Collectors.groupingBy(MatchStats::getRoundNumber));

        logger.info("Organize match stats: userId={}, matchId={}", userId, matchId);
        List<List<MatchStatsResponse>> organizedStats = new ArrayList<>();
        for (List<MatchStats> roundStats : statsByRound.values()) {
            List<MatchStatsResponse> roundResponses = roundStats.stream()
                    .map(MatchStatsResponse::from)
                    .collect(Collectors.toList());
            organizedStats.add(roundResponses);
        }

        logger.info("Match stats organized: userId={}, matchId={}", userId, matchId);
        return MatchStatsListResponse.builder()
                .stats(organizedStats)
                .build();
    }

    @Transactional
    public MatchStatsResponse createMatchStats(String userId, MatchStatsCreateRequest request) {
        logger.info("Create match stats: userId={}, request={}", userId, request);
        MatchParticipant participant = matchParticipantRepository.findById(request.getMatchParticipantId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_PARTICIPANT_ID));
        
        logger.info("Validate team manager role: userId={}, matchId={}", userId, participant.getMatch().getId());
        validateTeamManagerRole(userId, participant.getMatch().getId());

        if (request.getStatType() == StatType.ASSIST) {
            logger.info("Validate assist requires goal: assistedMatchStatId={}", request.getAssistedMatchStatId());
            if (request.getAssistedMatchStatId() == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST_ASSIST_REQUIRES_GOAL);
            }
            
            MatchStats assistedGoal = matchStatsRepository.findById(request.getAssistedMatchStatId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_STAT_ID));
            
            if (assistedGoal.getStatType() != StatType.GOAL) {
                throw new BusinessException(ErrorCode.BAD_REQUEST_ASSIST_REQUIRES_GOAL);
            }
        }

        logger.info("Save match stats: userId={}, matchId={}", userId, participant.getMatch().getId());
        MatchStats stats = matchStatsRepository.save(MatchStats.builder()
                .match(participant.getMatch())
                .matchParticipant(participant)
                .roundNumber(request.getRoundNumber())
                .statType(request.getStatType())
                .assistedMatchStatId(request.getAssistedMatchStatId())
                .historyTime(LocalDateTime.now())
                .build());

        logger.info("Match stats created: id={}, matchId={}", stats.getId(), participant.getMatch().getId());
        return MatchStatsResponse.from(stats);
    }

    @Transactional
    public void deleteMatchStats(String userId, String statId) {
        logger.info("Delete match stats: userId={}, statId={}", userId, statId);
        MatchStats stats = matchStatsRepository.findById(statId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_STAT_ID));

        logger.info("Validate team manager role: userId={}, matchId={}", userId, stats.getMatch().getId());
        validateTeamManagerRole(userId, stats.getMatch().getId());

        if (stats.getStatType() == StatType.GOAL && 
            matchStatsRepository.existsByAssistedMatchStatId(stats.getId())) {
            logger.info("Delete goal with assist: userId={}, statId={}", userId, statId);
            throw new BusinessException(ErrorCode.FORBIDDEN_DELETE_GOAL_WITH_ASSIST);
        }

        logger.info("Delete match stats: userId={}, statId={}", userId, statId);
        matchStatsRepository.delete(stats);
    }

    

    private void validateTeamManagerRole(String userId, String matchId) {
        logger.info("Validate team manager role: userId={}, matchId={}", userId, matchId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));
        
        Optional<Tuple> tuple = teamMemberRepository.findOneWithTeamByUserAndTeamIdAndIsDeletedFalse(userId, match.getTeam().getId());
        if (tuple.isEmpty()) {
            logger.info("Team member not found: userId={}, matchId={}", userId, matchId);
            throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
        }

        TeamMember teamMember = tuple.get().get(0, TeamMember.class);
        Team team = tuple.get().get(1, Team.class);

        AccessUtil.hasRequiredRole(teamMember.getId(), Optional.ofNullable(match.getSubstituteTeamMemberId()), teamMember.getRole(), team.getAccess());
    }

} 