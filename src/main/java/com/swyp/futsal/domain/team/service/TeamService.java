package com.swyp.futsal.domain.team.service;

import com.querydsl.core.Tuple;
import com.swyp.futsal.api.team.dto.CreateTeamRequest;
import com.swyp.futsal.api.team.dto.GetMyTeamResponse;
import com.swyp.futsal.api.team.dto.TeamMemberInfoResponse;
import com.swyp.futsal.api.team.dto.TeamSearchResponse;
import com.swyp.futsal.domain.common.enums.MemberStatus;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.entity.TeamMember;
import com.swyp.futsal.domain.team.repository.TeamRepository;
import com.swyp.futsal.domain.team.repository.TeamMemberRepository;
import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.domain.user.repository.UserRepository;
import com.swyp.futsal.provider.S3Provider;
import com.swyp.futsal.provider.PresignedUrlResponse;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final Logger logger = LoggerFactory.getLogger(TeamService.class);
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final S3Provider s3Provider;

    @Transactional
    public Team createTeam(String userId, CreateTeamRequest request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_ID));

            boolean isExists = teamRepository.existsByName(request.getName());
            if (isExists) {
                throw new BusinessException(ErrorCode.CONFLICT_NICKNAME_ALREADY_EXISTS);
            }

            logger.info("createTeam: user={}", user.getId());
            Team team = teamRepository.save(Team.builder()
                    .name(request.getName())
                    .introduction(request.getIntroduction())
                    .rule(request.getRule())
                    .matchType(request.getMatchType())
                    .access(request.getAccess())
                    .dues(request.getDues())
                    .user(user)
                    .build());

            logger.info("createTeam: team={}", team.getId());
            createTeamMember(team, user);
            return team;
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean isNameUnique(String name) {
        return !teamRepository.existsTeamByName(name);
    }

    public PresignedUrlResponse getLogoPresignedUrl(String teamId) {
        String path = String.format("teams/%s/logo", teamId);
        return s3Provider.getUploadPresignedUrl(path);
    }

    @Transactional
    public Optional<PresignedUrlResponse> updateTeamLogoById(String userId, String teamId, String logoUri) {
        try {
            if (!isTeamUpdateAccessable(userId, teamId)) {
                logger.error("updateTeamLogoById: userId={}, teamId={}, logoUri={}", userId, teamId, logoUri);
                throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
            }
            
            teamRepository.updateLogoById(teamId, logoUri);
            return s3Provider.getDownloadPresignedUrl(logoUri);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public GetMyTeamResponse getMyTeam(String userId) {
        try {
            Tuple tuple = teamMemberRepository.findOneWithTeamByUserAndIsDeletedFalse(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_ID));

            TeamMember teamMember = tuple.get(0, TeamMember.class);
            Team team = tuple.get(1, Team.class);
            
            if (team.getLogoUri() == null) {
                return new GetMyTeamResponse(team.getId(), teamMember.getId(), team.getName(), null, teamMember.getRole(), team.getAccess(), teamMember.getCreatedTime());
            }
            Optional<PresignedUrlResponse> logoUrl = s3Provider.getDownloadPresignedUrl(team.getLogoUri());
            Optional<String> logoUrlString = logoUrl.map(PresignedUrlResponse::getUrl);
            return new GetMyTeamResponse(team.getId(), teamMember.getId(), team.getName(), logoUrlString, teamMember.getRole(), team.getAccess(), teamMember.getCreatedTime());
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public TeamMemberInfoResponse getMyTeamMembers(String teamId) {
        List<Tuple> teamMemberInfo = teamMemberRepository.findTeamMembersInfoByTeamId(teamId);
        Team team = teamMemberInfo.get(0).get(1, Team.class);

        if (team == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_TEAM_ID);
        }

        List<TeamMemberInfoResponse.MemberInfo> members = teamMemberInfo.stream()
            .map(tuple -> {
                User user = tuple.get(2, User.class);
                TeamMember teamMember = tuple.get(0, TeamMember.class);
                MemberStatus status = teamMember.getStatus();
                TeamRole role = teamMember.getRole();

                return new TeamMemberInfoResponse.MemberInfo(user, status, role);
            })
            .toList();

        return new TeamMemberInfoResponse(team, members);
    }

    public List<TeamSearchResponse> searchTeams(String name) {
        List<Tuple> teams = teamRepository.findAllWithLeaderByNameContaining(name);
        List<String> teamIds = teams.stream()
                .map(tuple -> tuple.get(0, Team.class).getId())
                .collect(Collectors.toList());
        List<Tuple> teamMemberCounts = teamMemberRepository.countWithTeamIdByTeamIds(teamIds);
        return teams.stream()
                .map(tuple -> {
                    Team team = tuple.get(0, Team.class);
                    User leader = tuple.get(1, User.class);
                    return new TeamSearchResponse(
                        team.getId(),
                        team.getName(),
                        leader.getName(),
                        teamMemberCounts.stream()
                            .filter(t -> t.get(0, String.class).equals(team.getId()))
                            .findFirst()
                            .map(t -> t.get(1, Long.class).intValue())
                            .orElse(0),
                        team.getCreatedTime()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateMemberStatus(String authId, String teamId, String userId, MemberStatus memberStatus) {
        try {
            if (!isTeamMemberStatusUpdateAccessible(authId, teamId)) {
                logger.error("updateTeamLogoById: userId={}, teamId={}", authId, teamId);
                throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
            }
            teamMemberRepository.updateMemberStatus(teamId, userId, memberStatus);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void updateRoleTeamMember(String authId, String teamId, String userId, TeamRole newRole) {
        if (!isTeamMemberStatusUpdateAccessible(authId, teamId)) {
            logger.error("updateRoleTeamMember: Permission denied for userId={}, teamId={}", authId, teamId);
            throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
        }
        performRoleUpdate(teamId, userId, newRole);
    }

    @Transactional
    protected void performRoleUpdate(String teamId, String userId, TeamRole newRole) {
        TeamMember updateMember = teamMemberRepository.findByUserAndTeamAndIsDeletedFalse(userId, teamId)
            .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED));

        if (updateMember.getRole().equals(newRole)) {
            return;
        }

        if (newRole.equals(TeamRole.TEAM_LEADER)) {
            TeamMember currentLeader = teamMemberRepository.getTeamLeaderByTeamMember(teamId);
            if (currentLeader != null) {
                currentLeader.setRole(TeamRole.TEAM_MEMBER);
            }
        }

        updateMember.setRole(newRole);
    }

    private void createTeamMember(Team team, User user) {
        logger.info("createTeamMember: team={}, user={}", team.getId(), user.getId());
        teamMemberRepository.save(TeamMember.builder()
                .team(team)
                .user(user)
                .role(TeamRole.OWNER)
                .status(MemberStatus.ACTIVE)
                .isDeleted(false)
                .build());
    }

    private boolean isTeamUpdateAccessable(String userId, String teamId) {
        TeamMember teamMember = teamMemberRepository.findByUserAndTeamAndIsDeletedFalse(userId, teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST_INVALID_PARAMETER_VALUE));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_ID));
        
        if (team.getAccess() == TeamRole.TEAM_MEMBER) {
            return true;
        }

        return hasRequiredRole(teamMember.getRole(), team.getAccess());
    }

    private boolean isTeamMemberStatusUpdateAccessible(String userId, String teamId) {
        TeamMember teamMember = teamMemberRepository.findByUserAndTeamAndIsDeletedFalse(userId, teamId)
            .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST_INVALID_PARAMETER_VALUE));
      return teamMember.getRole() == TeamRole.OWNER;
    }

    private boolean hasRequiredRole(TeamRole memberRole, TeamRole requiredRole) {
        logger.info("hasRequiredRole: memberRole={}, requiredRole={}", memberRole, requiredRole);
        return switch (requiredRole) {
            case OWNER -> memberRole == TeamRole.OWNER;
            case TEAM_LEADER -> memberRole == TeamRole.OWNER || memberRole == TeamRole.TEAM_LEADER;
            case TEAM_DEPUTY_LEADER -> memberRole == TeamRole.OWNER || memberRole == TeamRole.TEAM_LEADER 
                    || memberRole == TeamRole.TEAM_DEPUTY_LEADER;
            case TEAM_SECRETARY -> memberRole == TeamRole.OWNER || memberRole == TeamRole.TEAM_DEPUTY_LEADER 
                    || memberRole == TeamRole.TEAM_SECRETARY;
            case TEAM_MEMBER -> true;
        };
    }
}