package com.swyp.futsal.domain.team.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.Tuple;
import com.swyp.futsal.api.team.dto.GetMyTeamMemberResponse;
import com.swyp.futsal.domain.common.enums.MatchStatus;
import com.swyp.futsal.domain.common.enums.StatType;
import com.swyp.futsal.domain.common.enums.SubTeam;
import com.swyp.futsal.domain.match.entity.Match;
import com.swyp.futsal.domain.match.entity.MatchParticipant;
import com.swyp.futsal.domain.match.entity.MatchStats;
import com.swyp.futsal.domain.match.repository.MatchParticipantRepository;
import com.swyp.futsal.domain.match.repository.MatchStatsRepository;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.entity.TeamMember;
import com.swyp.futsal.domain.team.repository.TeamMemberRepository;
import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.domain.user.repository.UserRepository;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import com.swyp.futsal.provider.PresignedUrlResponse;
import com.swyp.futsal.provider.S3Provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTeamMemberService {

    private final Logger logger = LoggerFactory.getLogger(TeamMemberService.class);
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final MatchStatsRepository matchStatsRepository;
    private final S3Provider s3Provider;


    public GetMyTeamMemberResponse execute_by_me(String userId) {
        logger.info("Get team member info by me");
        Tuple result = teamMemberRepository.findOneWithTeamByUserAndIsDeletedFalse(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_ID));

        TeamMember teamMember = result.get(0, TeamMember.class);
        Team team = result.get(1, Team.class);
        logger.info("Team member ID: {}, Team ID: {}", teamMember.getId(), team.getId());

        User user = getUserById(userId);
        GetMyTeamMemberResponse.MatchInfo matchInfo = getMatchInfoByTeamIdAndTeamMemberId(team.getId(), teamMember.getId());
        Optional<String> profileUrlString = getProfileUrl(user.getProfileUri());
        return GetMyTeamMemberResponse.builder()
            .id(teamMember.getId())
            .name(user.getName())
            .birthDate(user.getBirthDate())
            .generation(calculateGeneration(user.getBirthDate()))
            .squadNumber(teamMember.getSquadNumber())
            .profileUrl(profileUrlString)
            .team(new GetMyTeamMemberResponse.TeamInfo(team.getId(), team.getName(), teamMember.getRole().name()))
            .match(matchInfo)
            .build();
    }

    public GetMyTeamMemberResponse execute_by_team_member_id(String userId, String teamMemberId) {
        logger.info("Get team member info by team member ID: {}", teamMemberId);
        Tuple result = teamMemberRepository.findOneWithTeamByTeamMemberIdAndIsDeletedFalse(teamMemberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_MEMBER_ID));

        TeamMember teamMember = result.get(0, TeamMember.class);
        Team team = result.get(1, Team.class);
        logger.info("Team member ID: {}, Team ID: {}", teamMember.getId(), team.getId());

        logger.info("Check if user has permission to access team member info");
        teamMemberRepository.findByUserAndTeamAndIsDeletedFalse(userId, team.getId())
            .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED_TEAM_MEMBER_PERMISSION_REQUIRED));
        
        GetMyTeamMemberResponse.MatchInfo matchInfo = getMatchInfoByTeamIdAndTeamMemberId(team.getId(), teamMember.getId());
        User user = getUserById(teamMember.getUser().getId());
        Optional<String> profileUrlString = getProfileUrl(user.getProfileUri());
        return GetMyTeamMemberResponse.builder()
            .id(teamMember.getId())
            .name(user.getName())
            .birthDate(user.getBirthDate())
            .generation(calculateGeneration(user.getBirthDate()))
            .squadNumber(teamMember.getSquadNumber())
            .profileUrl(profileUrlString)
            .team(new GetMyTeamMemberResponse.TeamInfo(team.getId(), team.getName(), teamMember.getRole().name()))
            .match(matchInfo)
            .build();
    }

    private User getUserById(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_ID));

        logger.info("User ID: {}, User Name: {}", user.getId(), user.getName());
        return user;
    }

    private Optional<String> getProfileUrl(String profileUri) {
        Optional<PresignedUrlResponse> profileUrl = s3Provider.getDownloadPresignedUrl(profileUri);
        return Optional.ofNullable(profileUrl.map(PresignedUrlResponse::getUrl).orElse(null));
    }

    private GetMyTeamMemberResponse.MatchInfo getMatchInfoByTeamIdAndTeamMemberId(String teamId, String teamMemberId) {
        logger.info("Get match info by team ID: {}, team member ID: {}", teamId, teamMemberId);
        List<Tuple> result = matchParticipantRepository.findAllWithMatchByTeamMemberId(teamMemberId);
        Integer total = result.size();
        List<GetMyTeamMemberResponse.MatchInfo.MatchHistory> history = result.stream()
            .map(match -> {
                Match matchEntity = match.get(1, Match.class);
                MatchParticipant matchParticipant = match.get(0, MatchParticipant.class);
                String matchResult = determineMatchResult(matchEntity, matchParticipant);
                return new GetMyTeamMemberResponse.MatchInfo.MatchHistory(matchEntity.getId(), matchResult);
            })
            .collect(Collectors.toList());
        
        logger.info("Total matches: {}, History: {}", total, history);
        return new GetMyTeamMemberResponse.MatchInfo(total, history);
    }

    private String determineMatchResult(Match match, MatchParticipant matchParticipant) {
        List<Tuple> matchStats = matchStatsRepository.findAllWithMatchParticipantByMatchIdAndStatType(match.getId(), StatType.GOAL);
        if (matchStats.isEmpty() || match.getStatus() != MatchStatus.COMPLETED) {
            return "DRAW";
        }

        long teamAGoals = matchStats.stream()
            .filter(stat -> stat.get(1, MatchParticipant.class).getSubTeam() == SubTeam.A && stat.get(0, MatchStats.class).getStatType() == StatType.GOAL)
            .count();

        long teamBGoals = matchStats.stream()
            .filter(stat -> stat.get(1, MatchParticipant.class).getSubTeam() == SubTeam.B && stat.get(0, MatchStats.class).getStatType() == StatType.GOAL)
            .count();

        // 참가자의 팀에 따라 결과 결정
        if (matchParticipant.getSubTeam() == SubTeam.A) {
            if (teamAGoals > teamBGoals) return "WIN";
            if (teamAGoals < teamBGoals) return "LOSE";
            return "DRAW";
        } else {
            if (teamBGoals > teamAGoals) return "WIN";
            if (teamBGoals < teamAGoals) return "LOSE";
            return "DRAW";
        }
    }

    private String calculateGeneration(String birthDate) {
        if (birthDate == null) {
            return null;
        }

        LocalDate birthLocalDate = LocalDate.parse(birthDate);
        LocalDate now = LocalDate.now();
        
        int age = Period.between(birthLocalDate, now).getYears();
        
        if (age >= 20 && age < 30) {
            return "20대";
        } else if (age >= 30 && age < 40) {
            return "30대";
        } else if (age >= 40 && age < 50) {
            return "40대";
        } else if (age >= 50 && age < 60) {
            return "50대";
        } else if (age >= 60) {
            return "60대 이상";
        } else {
            return "10대 이하";
        }
    }
}
