package com.swyp.futsal.domain.team.service;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.Tuple;
import com.swyp.futsal.api.team.dto.GetAllTeamMemberResponse;
import com.swyp.futsal.api.team.dto.GetSubstituteTeamMemberResponse;
import com.swyp.futsal.domain.common.enums.MemberStatus;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.entity.TeamMember;
import com.swyp.futsal.domain.team.repository.TeamMemberRepository;
import com.swyp.futsal.domain.team.repository.TeamRepository;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import com.swyp.futsal.provider.PresignedUrlResponse;
import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.domain.user.repository.UserRepository;
import com.swyp.futsal.provider.S3Provider;
import com.swyp.futsal.util.service.AccessUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamMemberService {

    private final Logger logger = LoggerFactory.getLogger(TeamMemberService.class);
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final S3Provider s3Provider;

    public GetSubstituteTeamMemberResponse getActiveTeamMembers(String userId, String name, TeamRole role) {
        logger.info("Get active team members by user ID: {}, name: {}, role: {}", userId, name, role);
        
        Tuple result = teamMemberRepository.findOneWithTeamByUserAndIsDeletedFalse(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_MEMBER_ID));

        Team team = result.get(1, Team.class);
        List<Tuple> teamMembers = teamMemberRepository.findAllWithUserByTeamId(team.getId());
        
        logger.info("Team members: {}", teamMembers);
        
        List<GetSubstituteTeamMemberResponse.TeamMember> members = teamMembers.stream()
            .map(member -> {
                TeamMember teamMember = member.get(0, TeamMember.class);
                User user = member.get(1, User.class);
                Optional<String> profileUrl = getProfileUrl(user.getProfileUri());
                return GetSubstituteTeamMemberResponse.TeamMember.builder()
                    .id(teamMember.getId())
                    .name(user.getName())
                    .profileUrl(profileUrl)
                    .role(teamMember.getRole())
                    .createdTime(teamMember.getCreatedTime())
                    .build();
            })
            .filter(member -> {
                boolean nameMatch = name == null || member.getName().contains(name);
                boolean roleMatch = role == null || member.getRole().equals(role);
                return nameMatch && roleMatch;
            })
            .collect(Collectors.toList());

        return GetSubstituteTeamMemberResponse.builder()
            .members(members)
            .build();
    }

    public GetAllTeamMemberResponse getAllTeamMembers(String userId, String teamId) {
        logger.info("Get all team members by user ID: {}, team ID: {}", userId, teamId);
        Tuple result = teamMemberRepository.findOneWithTeamByUserAndIsDeletedFalse(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_MEMBER_ID));

        Team team = result.get(1, Team.class);
        List<Tuple> teamMembers = teamMemberRepository.findAllWithUserByTeamId(teamId);
        
        logger.info("Get all team members: {}", teamMembers);
        List<GetAllTeamMemberResponse.TeamMemberInfo> members = teamMembers.stream()
            .map(member -> {
                TeamMember teamMember = member.get(0, TeamMember.class);
                User user = member.get(1, User.class);
                
                return GetAllTeamMemberResponse.TeamMemberInfo.builder()
                    .id(teamMember.getId())
                    .name(user.getName())
                    .role(teamMember.getRole())
                    .birthDate(Optional.ofNullable(user.getBirthDate()))
                    .generation(user.calculateGeneration())
                    .profileUrl(getProfileUrl(user.getProfileUri()))
                    .gender(user.getGender())
                    .status(teamMember.getStatus())
                    .squadNumber(Optional.ofNullable(user.getSquadNumber()))
                    .createdTime(teamMember.getCreatedTime())
                    .build();
            })
            .sorted((m1, m2) -> {
                // First sort by status
                int statusCompare = m1.getStatus().compareTo(m2.getStatus());
                if (statusCompare != 0) {
                    return statusCompare;
                }
                
                // Then sort by role
                int roleCompare = m1.getRole().compareTo(m2.getRole());
                if (roleCompare != 0) {
                    return roleCompare;
                }
                
                // Finally sort by name
                return m1.getName().compareTo(m2.getName());
            })
            .collect(Collectors.toList());

        return GetAllTeamMemberResponse.builder()
            .teamId(teamId)
            .name(team.getName())
            .logoUrl(getProfileUrl(team.getLogoUri()))
            .teamMembers(members)
            .build();
    }

    @Transactional
    public void createTeamMember(String userId, String teamId, TeamRole role) {
        logger.info("Create team member by user ID: {}, team ID: {}, role: {}", userId, teamId, role);

        logger.info("Check if user joined another team");
        Optional<Tuple> teamMember = teamMemberRepository.findOneWithTeamByUserAndIsDeletedFalse(userId);
        if (teamMember.isPresent()) {
            return;
        }

        logger.info("Check if user is registered");
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_ID));

        logger.info("Check if team exists");
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_ID));

        logger.info("Create team member");
        teamMemberRepository.save(TeamMember.builder()
            .user(user)
            .team(team)
            .role(role)
            .status(MemberStatus.PENDING)
            .build());
    }

    @Transactional
    public void updateMemberStatus(String userId, String requestedTeamMemberId, MemberStatus memberStatus) {
        Tuple result = teamMemberRepository.findOneWithTeamByUserAndIsDeletedFalse(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_MEMBER_ID));

        TeamMember teamMember = result.get(0, TeamMember.class);
        Team team = result.get(1, Team.class);
        if (!AccessUtil.hasRequiredRole(teamMember.getId(), Optional.empty(), teamMember.getRole(), TeamRole.TEAM_SECRETARY)) {
            logger.error("updateRoleTeamMember: Permission denied for userId={}, teamId={}", userId, team.getId());
            throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
        }
        
        logger.info("Update member {} status to {}", requestedTeamMemberId, memberStatus);
        teamMemberRepository.updateStatusByIdAndRole(requestedTeamMemberId, TeamRole.TEAM_MEMBER, memberStatus);
    }

    @Transactional
    public void updateRoleByOwnerIdAndId(String userId, String requestedTeamMemberId, TeamRole newRole) {
        Tuple result = teamMemberRepository.findOneWithTeamByUserAndIsDeletedFalse(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_MEMBER_ID));

        TeamMember teamMember = result.get(0, TeamMember.class);
        Team team = result.get(1, Team.class);
        if (!Arrays.asList(TeamRole.OWNER, TeamRole.TEAM_LEADER).contains(teamMember.getRole())) {
            logger.error("updateRoleTeamMember: Permission denied for userId={}, teamId={}", userId, team.getId());
            throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
        }

        TeamMember requestedTeamMember = teamMemberRepository.findById(requestedTeamMemberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_MEMBER_ID));
        
        if (requestedTeamMember.getRole().equals(TeamRole.OWNER) || requestedTeamMember.getRole().equals(newRole) || newRole.equals(TeamRole.OWNER)) {
            logger.error("updateRoleTeamMember: Permission denied for userId={}, teamId={} if role is owner", userId, team.getId());
            return;
        }

        if (newRole.equals(TeamRole.TEAM_LEADER) || requestedTeamMember.getRole().equals(TeamRole.TEAM_LEADER)) {
            logger.error("updateRoleTeamMember: Permission denied for userId={}, teamId={} if role is team leader", userId, team.getId());
            return;
        }

        if (Arrays.asList(TeamRole.TEAM_DEPUTY_LEADER, TeamRole.TEAM_SECRETARY).contains(newRole)) {
            logger.info("Change role to team deputy leader or team secretary");
            Optional<TeamMember> currentTeamMember = teamMemberRepository.findOneByTeamIdAndRole(team.getId(), newRole);
            if (currentTeamMember.isPresent()) {
                logger.info("Change role to an existing team member {} into {}", currentTeamMember.get().getRole(), TeamRole.TEAM_MEMBER);
                teamMemberRepository.updateRoleById(currentTeamMember.get().getId(), TeamRole.TEAM_MEMBER);
            }
        }

        teamMemberRepository.updateRoleById(requestedTeamMemberId, newRole);
    }

    @Transactional
    public void cancelTeamMember(String userId, String teamMemberId) {
        Tuple result = teamMemberRepository.findOneWithTeamByUserAndIsDeletedFalse(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_MEMBER_ID));

        TeamMember teamMember = result.get(0, TeamMember.class);
        Team team = result.get(1, Team.class);

        if (teamMember.getId() != teamMemberId) {
            logger.error("cancelTeamMember: Permission denied for userId={}, teamId={}", userId, team.getId());
            throw new BusinessException(ErrorCode.UNAUTHORIZED_TO_REMOVE_TEAM_MEMBER);
        }

        if (teamMember.getRole().equals(TeamRole.TEAM_MEMBER) && teamMember.getStatus().equals(MemberStatus.PENDING)) {
            logger.info("Delete team member");
            teamMemberRepository.deleteById(teamMemberId);
        } 
    }

    @Transactional
    public void deleteTeamMember(String userId, String teamMemberId) {
        Tuple result = teamMemberRepository.findOneWithTeamByUserAndIsDeletedFalse(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_MEMBER_ID));

        TeamMember teamMember = result.get(0, TeamMember.class);
        Team team = result.get(1, Team.class);

        if (!AccessUtil.hasRequiredRole(teamMember.getId(), Optional.empty(), teamMember.getRole(), TeamRole.TEAM_SECRETARY)) {
            logger.error("deleteTeamMember: Permission denied for userId={}, teamId={}", userId, team.getId());
            throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
        }
        
        TeamMember requestedTeamMember = teamMemberRepository.findById(teamMemberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEAM_MEMBER_ID));

        if (Arrays.asList(TeamRole.OWNER, TeamRole.TEAM_LEADER, TeamRole.TEAM_DEPUTY_LEADER, TeamRole.TEAM_SECRETARY).contains(requestedTeamMember.getRole())) {
            logger.error("deleteTeamMember: Permission denied for userId={}, teamId={} if role is owner", userId, team.getId());
            return;
        }
        
        if (requestedTeamMember.getRole().equals(TeamRole.TEAM_MEMBER) && requestedTeamMember.getStatus().equals(MemberStatus.PENDING)) {
            logger.info("Delete team member");
            teamMemberRepository.deleteById(teamMemberId);
        } 
    }

    private Optional<String> getProfileUrl(String profileUri) {
        Optional<PresignedUrlResponse> profileUrl = s3Provider.getDownloadPresignedUrl(profileUri);
        return Optional.ofNullable(profileUrl.map(PresignedUrlResponse::getUrl).orElse(null));
    }
    
    
}
