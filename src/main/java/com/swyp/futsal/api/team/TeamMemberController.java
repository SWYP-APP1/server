package com.swyp.futsal.api.team;

import com.swyp.futsal.api.team.dto.GetMyTeamMemberResponse;
import com.swyp.futsal.api.team.dto.TeamMemberInfoResponse;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.domain.team.service.TeamService;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;
import com.swyp.futsal.domain.team.service.GetTeamMemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team-members")
@RequiredArgsConstructor
public class TeamMemberController {
    private final AuthService authService;
    private final TeamService teamService;
    private final GetTeamMemberService getTeamMemberService;

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

    @GetMapping("/team/{teamId}")
    public ApiResponse<TeamMemberInfoResponse> getMyTeamMembers(
            @RequestHeader("Authorization") String authorization, 
            @PathVariable String teamId) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(teamService.getMyTeamMembers(teamId));
    }

    private String getUserIdByHeader(String authorization) {
        String token = RequestUtil.getAuthorizationToken(authorization);
        return authService.getUserId(token);
    }
}
