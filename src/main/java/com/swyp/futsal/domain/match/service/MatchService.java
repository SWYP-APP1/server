package com.swyp.futsal.domain.match.service;

import com.querydsl.core.Tuple;
import com.swyp.futsal.api.match.dto.MatchCreateRequest;
import com.swyp.futsal.api.match.dto.MatchResponse;
import com.swyp.futsal.api.match.dto.MatchRoundsUpdateRequest;
import com.swyp.futsal.api.match.dto.MatchDetailResponse;
import com.swyp.futsal.domain.common.enums.MatchStatus;
import com.swyp.futsal.domain.common.enums.VoteStatus;
import com.swyp.futsal.domain.match.entity.Match;
import com.swyp.futsal.domain.match.repository.MatchRepository;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.entity.TeamMember;
import com.swyp.futsal.domain.team.repository.TeamMemberRepository;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import com.swyp.futsal.util.service.AccessUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {
    private final Logger logger = LoggerFactory.getLogger(MatchService.class);
    private final MatchRepository matchRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional(readOnly = true)
    public List<MatchResponse> getMatches(String userId, String teamId, Pageable pageable) {
        logger.info("getMatches: userId={}, teamId={}, pageable={}", userId, teamId, pageable);
        if (teamId == null) {
            logger.error("Team ID is null");
            throw new BusinessException(ErrorCode.BAD_REQUEST_INVALID_PARAMETER_VALUE);
        }

        Optional<TeamMember> teamMember = teamMemberRepository.findByUserAndTeamAndIsDeletedFalse(userId, teamId);
        if (teamMember.isEmpty()) {
            logger.error("User is not a member of the team: userId={}, teamId={}", userId, teamId);
            throw new BusinessException(ErrorCode.FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED);
        }

        logger.info("Start to get matches: teamId={}, pageable={}", teamId, pageable);
        return matchRepository.findAllByTeamIdOrderByMatchDateDesc(teamId, pageable)
                .map(MatchResponse::from)
                .toList();
    }

    @Transactional
    public MatchResponse createMatch(String userId, MatchCreateRequest request) {
        logger.info("createMatch: userId={}, request={}", userId, request);
        Tuple tuple = teamMemberRepository.findOneWithTeamByUserAndTeamIdAndIsDeletedFalse(userId, request.getTeamId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED));

        TeamMember teamMember = tuple.get(0, TeamMember.class);
        Team team = tuple.get(1, Team.class);

        if (!AccessUtil.hasRequiredRole(teamMember.getId(), null, teamMember.getRole(), team.getAccess())) {
            logger.error("createMatch: userId={}, request={}, error={}", userId, request, ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
            throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
        }

        if (request.getSubstituteTeamMemberId() != null) {
            logger.info("Check if substitute team member is valid");
            teamMemberRepository.findById(request.getSubstituteTeamMemberId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST_INVALID_PARAMETER_VALUE));
        }

        return MatchResponse.from(matchRepository.save(request.toMatch(team)));
    }

    @Transactional
    public void deleteMatch(String id, String userId) {
        logger.info("Start to delete match: id={}, userId={}", id, userId);
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));
        
        if (match.getStatus() == MatchStatus.COMPLETED) {
            logger.error("Match is already completed: id={}, userId={}, error={}", id, userId, ErrorCode.FORBIDDEN_DELETE_COMPLETED_MATCH);
            throw new BusinessException(ErrorCode.FORBIDDEN_DELETE_COMPLETED_MATCH);
        }

        Tuple tuple = teamMemberRepository.findOneWithTeamByUserAndTeamIdAndIsDeletedFalse(userId, match.getTeam().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED));

        TeamMember teamMember = tuple.get(0, TeamMember.class);
        Team team = tuple.get(1, Team.class);
        
        if (!AccessUtil.hasRequiredRole(teamMember.getId(), null, teamMember.getRole(), team.getAccess())) {
            logger.error("User does not have permission to delete match: id={}, userId={}, error={}", id, userId, ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
            throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
        }
        
        matchRepository.delete(match);
        logger.info("Match deleted successfully: id={}, userId={}", id, userId);
    }

    @Transactional
    public void updateRounds(String userId, String matchId, MatchRoundsUpdateRequest request) {
        logger.info("updateRounds: userId={}, matchId={}, request={}", userId, matchId, request);
        this.validateTeamManagerRole(userId, matchId);
        matchRepository.updateRoundsById(matchId, request.getRounds());
    }

    @Transactional(readOnly = true)
    public MatchDetailResponse getMatchDetail(String userId, String matchId) {
        logger.info("getMatchDetail: userId={}, matchId={}", userId, matchId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));

        teamMemberRepository.findByUserAndTeamAndIsDeletedFalse(userId, match.getTeam().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED));

        return MatchDetailResponse.from(match);
    }

    @Transactional
    public void updateVoteStatusToEnded(String userId, String matchId) {
        logger.info("Update vote status to ended: userId={}, matchId={}", userId, matchId);
        Match match = validateTeamManagerRole(userId, matchId);
        matchRepository.updateVoteStatusById(match.getId(), VoteStatus.ENDED);
        logger.info("Vote status updated to ended successfully: matchId={}", matchId);
    }

    @Transactional
    public void updateStatusToOngoing(String userId, String matchId) {
        logger.info("Update match status to ongoing: userId={}, matchId={}", userId, matchId);
        Match match = validateTeamManagerRole(userId, matchId);
        matchRepository.updateStatusById(match.getId(), MatchStatus.ONGOING);
        logger.info("Match status updated to ongoing successfully: matchId={}", matchId);
    }

    private Match validateTeamManagerRole(String userId, String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));

        Tuple tuple = teamMemberRepository.findOneWithTeamByUserAndTeamIdAndIsDeletedFalse(userId, match.getTeam().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED));

        TeamMember teamMember = tuple.get(0, TeamMember.class);
        Team team = tuple.get(1, Team.class);

        if (!AccessUtil.hasRequiredRole(teamMember.getId(), Optional.ofNullable(match.getSubstituteTeamMemberId()), teamMember.getRole(), team.getAccess())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
        }

        return match;
    }
} 