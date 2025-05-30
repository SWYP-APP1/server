package com.swyp.futsal.domain.match.service;

import com.querydsl.core.Tuple;
import com.swyp.futsal.api.match.dto.*;
import com.swyp.futsal.domain.common.enums.StatType;
import com.swyp.futsal.domain.common.enums.SubTeam;
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

        // Group stats by round number and team
        Map<Integer, Map<SubTeam, List<MatchStats>>> statsByRoundAndTeam = stats.stream()
                .collect(Collectors.groupingBy(
                    MatchStats::getRoundNumber,
                    Collectors.groupingBy(stat -> stat.getMatchParticipant().getSubTeam())
                ));

        // Create the response structure
        Map<Integer, MatchStatsListResponse.RoundStats> organizedStats = new HashMap<>();
        
        statsByRoundAndTeam.forEach((roundNumber, teamStats) -> {
            List<MatchStatsListResponse.RoundStats.GoalAssistPair> teamAPairs = createGoalAssistPairs(teamStats.getOrDefault(SubTeam.A, new ArrayList<>()));
            List<MatchStatsListResponse.RoundStats.GoalAssistPair> teamBPairs = createGoalAssistPairs(teamStats.getOrDefault(SubTeam.B, new ArrayList<>()));
            
            MatchStatsListResponse.RoundStats roundStats = MatchStatsListResponse.RoundStats.builder()
                    .teamA(teamAPairs)
                    .teamB(teamBPairs)
                    .build();
                    
            organizedStats.put(roundNumber, roundStats);
        });

        logger.info("Match stats organized: userId={}, matchId={}", userId, matchId);
        return MatchStatsListResponse.builder()
                .stats(organizedStats)
                .build();
    }

    private List<MatchStatsListResponse.RoundStats.GoalAssistPair> createGoalAssistPairs(List<MatchStats> teamStats) {
        List<MatchStatsListResponse.RoundStats.GoalAssistPair> pairs = new ArrayList<>();
        
        // First, create a map of goals and their assists
        Map<String, MatchStats> assistsByGoalId = teamStats.stream()
                .filter(stat -> stat.getStatType() == StatType.ASSIST)
                .collect(Collectors.toMap(
                    MatchStats::getAssistedMatchStatId,
                    assist -> assist
                ));

        // Create pairs for each goal
        teamStats.stream()
                .filter(stat -> stat.getStatType() == StatType.GOAL)
                .sorted(Comparator.comparing(MatchStats::getHistoryTime))
                .forEach(goal -> {
                    MatchStats assist = assistsByGoalId.get(goal.getId());
                    pairs.add(MatchStatsListResponse.RoundStats.GoalAssistPair.builder()
                            .goal(MatchStatsResponse.from(goal))
                            .assist(assist != null ? MatchStatsResponse.from(assist) : null)
                            .build());
                });

        return pairs;
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
    public List<MatchStatsResponse> createMatchStatsBulk(String userId, MatchStatsCreateBulkRequest request) {
        logger.info("Create match stats bulk: userId={}, request={}", userId, request);
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));

        validateTeamManagerRole(userId, match.getId());

        logger.info("Check if all participants are valid");
        List<String> goalParticipantIds = request.getStats().stream()
                .map(MatchStatsCreateBulkRequest.MatchStatsCreateRequest::getGoalMatchParticipantId)
                .collect(Collectors.toList());
        List<String> assistParticipantIds = request.getStats().stream()
                .map(MatchStatsCreateBulkRequest.MatchStatsCreateRequest::getAssistMatchParticipantId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        Set<String> allParticipantIds = new HashSet<>(goalParticipantIds);
        allParticipantIds.addAll(assistParticipantIds);

        List<MatchParticipant> participants = matchParticipantRepository.findAllById(allParticipantIds);
        if (participants.size() != allParticipantIds.size()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_MATCH_PARTICIPANT_ID);
        }

        // Validate subTeam for each participant
        for (MatchStatsCreateBulkRequest.MatchStatsCreateRequest stat : request.getStats()) {
            MatchParticipant goalParticipant = participants.stream()
                    .filter(p -> p.getId().equals(stat.getGoalMatchParticipantId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_PARTICIPANT_ID));

            if (!goalParticipant.getSubTeam().equals(stat.getSubTeam())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST_INVALID_SUB_TEAM);
            }

            if (stat.getAssistMatchParticipantId().isPresent()) {
                MatchParticipant assistParticipant = participants.stream()
                        .filter(p -> p.getId().equals(stat.getAssistMatchParticipantId().get()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_PARTICIPANT_ID));

                if (!assistParticipant.getSubTeam().equals(stat.getSubTeam())) {
                    throw new BusinessException(ErrorCode.BAD_REQUEST_INVALID_SUB_TEAM);
                }
            }
        }

        logger.info("Create match stats bulk: userId={}, request={}", userId, request);
        List<MatchStatsResponse> stats = new ArrayList<>();
        for (MatchStatsCreateBulkRequest.MatchStatsCreateRequest stat : request.getStats()) {
            Optional<MatchParticipant> goalParticipant = participants.stream()
                    .filter(p -> p.getId().equals(stat.getGoalMatchParticipantId()))
                    .findFirst();
            Optional<MatchParticipant> assistParticipant = participants.stream()
                    .filter(p -> p.getId().equals(stat.getAssistMatchParticipantId().orElse(null)))
                    .findFirst();

            if (goalParticipant.isPresent()) {
                MatchStats matchStat = matchStatsRepository.save(MatchStats.builder()
                    .match(match)
                    .matchParticipant(goalParticipant.get())
                    .roundNumber(stat.getRoundNumber())
                    .statType(StatType.GOAL)
                    .historyTime(LocalDateTime.now())
                    .build());
                stats.add(MatchStatsResponse.from(matchStat));
                
                if (assistParticipant.isPresent()) {
                    MatchStats assistStat = matchStatsRepository.save(MatchStats.builder()
                        .match(match)
                        .matchParticipant(assistParticipant.get())
                        .roundNumber(stat.getRoundNumber())
                        .statType(StatType.ASSIST)
                        .assistedMatchStatId(matchStat.getId())
                        .historyTime(LocalDateTime.now())
                        .build());
                    stats.add(MatchStatsResponse.from(assistStat));
                }
            }
        }
        
        return stats;
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