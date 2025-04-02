package com.swyp.futsal.api.team;

import com.swyp.futsal.api.team.dto.CreateTeamRequest;
import com.swyp.futsal.api.team.dto.GetMyTeamResponse;
import com.swyp.futsal.api.team.dto.TeamResponse;
import com.swyp.futsal.api.team.dto.UpdateTeamLogoRequest;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.service.TeamService;

import com.swyp.futsal.provider.PresignedUrlResponse;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final AuthService authService;
    private final TeamService teamService;

    @PostMapping("")
    public ApiResponse<TeamResponse> createTeam(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateTeamRequest request) {
        String userId = getUserIdByHeader(authorization);
        Team team = teamService.createTeam(userId, request);
        return ApiResponse.success(new TeamResponse(team));
    }

    @GetMapping("/me")
    public ApiResponse<GetMyTeamResponse> getMyTeam(@RequestHeader("Authorization") String authorization) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(teamService.getMyTeam(userId));
    }

    @GetMapping("/check-nickname")
    public ApiResponse<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean isUnique = teamService.isNameUnique(nickname);
        return ApiResponse.success(Map.of("unique", isUnique));
    }

    @GetMapping("/{teamId}/logo-presigned-url")
    public ApiResponse<PresignedUrlResponse> getLogoPresignedUrl(@RequestHeader("Authorization") String authorization, @PathVariable String teamId) {
        getUserIdByHeader(authorization);
        return ApiResponse.success(teamService.getLogoPresignedUrl(teamId));
    }

    @PatchMapping("{teamId}/logo")
    public ApiResponse<Optional<PresignedUrlResponse>> updateTeamLogo(
            @RequestHeader("Authorization") String authorization,   
            @PathVariable String teamId,
            @RequestBody UpdateTeamLogoRequest request) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(teamService.updateTeamLogoById(userId, teamId, request.getUri()));
    }

    

    @GetMapping("")
    public ApiResponse<List<TeamResponse>> searchTeams(@RequestParam String name) {
        List<Team> teams = teamService.searchTeams(name);
        List<TeamResponse> responses = teams.stream()
                .map(TeamResponse::new)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    private String getUserIdByHeader(String authorization) {
        String token = RequestUtil.getAuthorizationToken(authorization);
        return authService.getUserId(token);
    }
}