package com.swyp.futsal.domain.match.service;

import com.querydsl.core.Tuple;
import com.swyp.futsal.api.match.dto.*;
import com.swyp.futsal.domain.common.enums.ParticipationStatus;
import com.swyp.futsal.domain.common.enums.SubTeam;
import com.swyp.futsal.domain.match.entity.Match;
import com.swyp.futsal.domain.match.entity.MatchParticipant;
import com.swyp.futsal.domain.match.repository.MatchParticipantRepository;
import com.swyp.futsal.domain.match.repository.MatchRepository;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.entity.TeamMember;
import com.swyp.futsal.domain.team.repository.TeamMemberRepository;
import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.domain.user.repository.UserRepository;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import com.swyp.futsal.provider.PresignedUrlResponse;
import com.swyp.futsal.provider.S3Provider;
import com.swyp.futsal.util.service.AccessUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchParticipantService {

    private final Logger logger = LoggerFactory.getLogger(MatchParticipantService.class);
    private final UserRepository userRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final MatchRepository matchRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final S3Provider s3Provider;

    @Transactional
    public CreateMatchParticipantResponse registerParticipants(String userId, MatchParticipantRequest request) {
        logger.info("Start to register participants: userId={}, request={}", userId, request);
        Match match = validateTeamManagerRole(userId, request.getMatchId());

        logger.info("Validated team manager role: match={}", match);
        List<TeamMember> teamMembers = teamMemberRepository.findTeamMembersByTeamIdAndMemberIds(match.getTeam().getId(), request.getTeamMemberIds());
        List<MatchParticipant> participants = matchParticipantRepository.saveAll(teamMembers.stream()
                .map(member -> MatchParticipant.builder()
                        .match(match)
                        .teamMember(member)
                        .subTeam(SubTeam.NONE)
                        .status(ParticipationStatus.YES)
                        .build())
                .collect(Collectors.toList()));

        return createMatchParticipantResponse(participants);
    }

    @Transactional
    public void deleteParticipants(String userId, String matchId, MatchParticipantDeleteRequest request) {
        logger.info("Start to delete participants: userId={}, matchId={}, request={}", userId, matchId, request);
        validateTeamManagerRole(userId, matchId);

        logger.info("Deleting participants: ids={}", request.getIds());
        matchParticipantRepository.deleteAllById(request.getIds());
    }

    @Transactional
    public void updateSubTeam(String userId, String matchId, SubTeamUpdateRequest request) {
        logger.info("Start to update sub team: userId={}, matchId={}, request={}", userId, matchId, request);
        validateTeamManagerRole(userId, matchId);

        logger.info("Updating sub team: ids={}, subTeam={}", request.getIds(), request.getSubTeam());
        List<MatchParticipant> participants = matchParticipantRepository.findAllByIdsAndMatchId(request.getIds(), matchId);
        participants.forEach(participant -> participant.updateSubTeam(request.getSubTeam()));

        logger.info("Saving participants: participants={}", participants);
        matchParticipantRepository.saveAll(participants);
    }

    private CreateMatchParticipantResponse createMatchParticipantResponse(List<MatchParticipant> participants) {
        return CreateMatchParticipantResponse.builder()
                .participants(getSortedParticipants(participants))
                .build();
    }

    private List<CreateMatchParticipantResponse.Participant> getSortedParticipants(List<MatchParticipant> participants) {
        List<Tuple> users = userRepository.findAllWithTeamMemberByTeamMemberIds(participants.stream()
                .map(p -> p.getTeamMember().getId())
                .collect(Collectors.toList()));

        List<CreateMatchParticipantResponse.Participant> participantList = new ArrayList<>();
        for (MatchParticipant participant : participants) {
            User user = users.stream()
                    .filter(u -> u.get(1, TeamMember.class).getId().equals(participant.getTeamMember().getId()))
                    .findFirst()
                    .map(t -> t.get(0, User.class))
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_ID));
            participantList.add(getParticipant(participant, user));
        }
        return participantList.stream()
                .sorted(compareBySubTeamAndName())
                .collect(Collectors.toList());
    }

    private CreateMatchParticipantResponse.Participant getParticipant(MatchParticipant participant, User user) {
        Optional<String> profileUri = Optional.ofNullable(user.getProfileUri());
        if (profileUri.isPresent()) {
            Optional<PresignedUrlResponse> profileUrl = s3Provider.getDownloadPresignedUrl(profileUri.get());
            if (profileUrl.isPresent()) {
                return CreateMatchParticipantResponse.Participant.from(profileUrl.get().getUrl(), participant);
            } else {
                return CreateMatchParticipantResponse.Participant.from(null, participant);
            }
        } else {
            return CreateMatchParticipantResponse.Participant.from(null, participant);
        }
    }

    private Comparator<CreateMatchParticipantResponse.Participant> compareBySubTeamAndName() {
        return Comparator
                .comparing(CreateMatchParticipantResponse.Participant::getSubTeam)
                .thenComparing(p -> p.getName());
    }

    private Match validateTeamManagerRole(String userId, String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));
        
        Optional<Tuple> teamMemberAndTeam = teamMemberRepository.findOneWithTeamByUserAndTeamIdAndIsDeletedFalse(userId, match.getTeam().getId());
        if (teamMemberAndTeam.isEmpty()) {
            logger.error("Team member not found: userId={}, matchId={}", userId, matchId);
            throw new BusinessException(ErrorCode.FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED);
        }

        TeamMember teamMember = teamMemberAndTeam.get().get(0, TeamMember.class);
        Team team = teamMemberAndTeam.get().get(1, Team.class);

        if (!AccessUtil.hasRequiredRole(teamMember.getRole(), team.getAccess())) {
            logger.error("Team leader permission required: userId={}, matchId={}", userId, matchId);
            throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
        } 

        return match;
    }

    @Transactional(readOnly = true)
    public MatchParticipantListResponse getMatchParticipants(String userId, String matchId) {
        logger.info("Get match participants by userId={}, matchId={}", userId, matchId);
        
        // 매치 존재 여부 확인
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));

        // 팀 멤버 검증
        logger.info("Validate if this user is a team member: userId={}, teamId={}", userId, match.getTeam().getId());
        TeamMember teamMember = teamMemberRepository.findByUserAndTeamAndIsDeletedFalse(userId, match.getTeam().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED));

        // 참가자 목록 조회
        logger.info("Get participants for match: matchId={}", matchId);
        List<MatchParticipant> participants = matchParticipantRepository.findAllByMatchId(matchId);
        
        // 사용자 정보 조회
        logger.info("Get user information for participants: matchId={}", matchId);
        List<String> teamMemberIds = participants.stream()
                .map(p -> p.getTeamMember().getId())
                .collect(Collectors.toList());
        
        logger.info("Get user information for participants: teamMemberIds={}", teamMemberIds);
        List<Tuple> users = userRepository.findAllWithTeamMemberByTeamMemberIds(teamMemberIds);
        
        // 참가자 정보 변환
        logger.info("Convert participants to response format: matchId={}", matchId);
        List<MatchParticipantListResponse.Participant> participantList = new ArrayList<>();
        for (MatchParticipant participant : participants) {
            User user = users.stream()
                    .filter(u -> u.get(1, TeamMember.class).getId().equals(participant.getTeamMember().getId()))
                    .findFirst()
                    .map(t -> t.get(0, User.class))
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_ID));

            String profileUrl = null;
            if (user.getProfileUri() != null) {
                Optional<PresignedUrlResponse> presignedUrl = s3Provider.getDownloadPresignedUrl(user.getProfileUri());
                profileUrl = presignedUrl.map(PresignedUrlResponse::getUrl).orElse(null);
            }

            participantList.add(MatchParticipantListResponse.Participant.from(participant, user, profileUrl));
        }

        // 정렬: A팀(이름순) > B팀(이름순)
        logger.info("Sort participants: matchId={}", matchId);
        participantList.sort(MatchParticipantListResponse.participantComparator());

        // 본인 참가 여부 확인
        boolean isParticipate = participants.stream()
                .anyMatch(p -> p.getTeamMember().getId().equals(teamMember.getId()));

        return MatchParticipantListResponse.builder()
                .participants(participantList)
                .isParticipate(isParticipate)
                .build();
    }

    @Transactional(readOnly = true)
    public MatchParticipantMomResponse getMomCandidates(String userId, String matchId) {
        logger.info("Get MOM candidates: userId={}, matchId={}", userId, matchId);
        
        // 매치 존재 여부 확인
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));

        // 팀 멤버 검증
        logger.info("Validate if this user is a team member: userId={}, teamId={}", userId, match.getTeam().getId());
        TeamMember currentTeamMember = teamMemberRepository.findByUserAndTeamAndIsDeletedFalse(userId, match.getTeam().getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED));

        // 참가자 목록 조회 (본인 제외)
        logger.info("Get participants for match (excluding current user): matchId={}", matchId);
        List<MatchParticipant> participants = matchParticipantRepository.findAllByMatchIdAndTeamMemberIdNot(
                matchId, 
                currentTeamMember.getId()
        );
        
        // 사용자 정보 조회
        logger.info("Get user information for participants: matchId={}", matchId);
        List<String> teamMemberIds = participants.stream()
                .map(p -> p.getTeamMember().getId())
                .collect(Collectors.toList());
        
        List<Tuple> users = userRepository.findAllWithTeamMemberByTeamMemberIds(teamMemberIds);
        
        // 참가자 정보 변환
        List<MatchParticipantMomResponse.Participant> participantList = participants.stream()
                .map(participant -> {
                    User user = users.stream()
                            .filter(u -> u.get(1, TeamMember.class).getId().equals(participant.getTeamMember().getId()))
                            .findFirst()
                            .map(t -> t.get(0, User.class))
                            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_ID));
                    
                    return MatchParticipantMomResponse.Participant.from(participant, user);
                })
                .sorted(Comparator.comparing(MatchParticipantMomResponse.Participant::getName))
                .collect(Collectors.toList());

        return MatchParticipantMomResponse.builder()
                .matchId(matchId)
                .participants(participantList)
                .build();
    }
}