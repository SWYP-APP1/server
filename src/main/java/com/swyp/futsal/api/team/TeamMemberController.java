package com.swyp.futsal.api.team;

import com.swyp.futsal.api.team.dto.GetAllTeamMemberResponse;
import com.swyp.futsal.api.team.dto.GetMyTeamMemberResponse;
import com.swyp.futsal.api.team.dto.GetSubstituteTeamMemberResponse;
import com.swyp.futsal.api.team.dto.JoinTeamRequest;
import com.swyp.futsal.api.team.dto.UpdateTeamRoleRequest;
import com.swyp.futsal.api.team.dto.UpdateTeamMemberStatusRequest;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.domain.common.enums.MemberStatus;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;

import jakarta.validation.Valid;

import com.swyp.futsal.domain.team.service.GetTeamMemberService;
import com.swyp.futsal.domain.team.service.TeamMemberService;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team-members")
@RequiredArgsConstructor
public class TeamMemberController {
    private final AuthService authService;
    private final TeamMemberService teamMemberService;
    private final GetTeamMemberService getTeamMemberService;

    @GetMapping
    public ApiResponse<GetAllTeamMemberResponse> getAllTeamMembers(
        @RequestHeader("Authorization") String authorization,
        @Valid @RequestParam(required = true) String teamId) 
    {
        if (teamId.length() != 36) {
            throw new BusinessException(ErrorCode.BAD_REQUEST_INVALID_PARAMETER_VALUE);
        }

        String userId = getUserIdByHeader(authorization);
        GetAllTeamMemberResponse response = teamMemberService.getAllTeamMembers(userId, teamId);
        return ApiResponse.success(response);
    }

    @GetMapping("/active")
    public ApiResponse<GetSubstituteTeamMemberResponse> getActiveTeamMembers(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = true) String name,
            @RequestParam(required = true) TeamRole role) {
        String userId = getUserIdByHeader(authorization);
        GetSubstituteTeamMemberResponse response = teamMemberService.getActiveTeamMembers(userId, name, role);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    public ApiResponse<GetMyTeamMemberResponse> getMyInfo(@RequestHeader("Authorization") String authorization) {
        String userId = getUserIdByHeader(authorization);
        GetMyTeamMemberResponse response = getTeamMemberService.execute_by_me(userId);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<GetMyTeamMemberResponse> getTeamMemberInfo(
            @RequestHeader("Authorization") String authorization, 
            @PathVariable String id) {
        String userId = getUserIdByHeader(authorization);
        GetMyTeamMemberResponse response = getTeamMemberService.execute_by_team_member_id(userId, id);
        return ApiResponse.success(response);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createTeamMember(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody JoinTeamRequest request
    ) {
        String userId = getUserIdByHeader(authorization);
        teamMemberService.createTeamMember(userId, request.getTeamId(), TeamRole.TEAM_MEMBER);
    }

    @PatchMapping("/{id}/status/accepted")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTeamMemberStatusAccepted(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String id
    ) {
        String userId = getUserIdByHeader(authorization);
        teamMemberService.updateMemberStatus(userId, id, MemberStatus.ACTIVE);
    }

    @PatchMapping("/{id}/status/declined")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTeamMemberStatusDeclined(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String id
    ) {
        String userId = getUserIdByHeader(authorization);
        teamMemberService.updateMemberStatus(userId, id, MemberStatus.DECLINED);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTeamMemberStatus(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String id,
        @Valid @RequestBody UpdateTeamMemberStatusRequest request
    ) {
        String userId = getUserIdByHeader(authorization);
        if (Arrays.asList(MemberStatus.ACTIVE, MemberStatus.INACTIVE).contains(request.getStatus())) {
            teamMemberService.updateMemberStatus(userId, id, request.getStatus());
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST_INVALID_PARAMETER_VALUE);
        }
    }

    @PatchMapping("/{id}/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTeamMemberRole(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String id,
        @Valid @RequestBody UpdateTeamRoleRequest request
    ) {
        String userId = getUserIdByHeader(authorization);
        teamMemberService.updateRoleByOwnerIdAndId(userId, id, request.getRole());
    }

    private String getUserIdByHeader(String authorization) {
        String token = RequestUtil.getAuthorizationToken(authorization);
        return authService.getUserId(token);
    }
}
