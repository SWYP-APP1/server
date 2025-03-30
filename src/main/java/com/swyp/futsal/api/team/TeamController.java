package com.swyp.futsal.api.team;

import com.swyp.futsal.api.team.dto.CreateTeamRequest;
import com.swyp.futsal.api.team.dto.TeamResponse;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.service.TeamService;
import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.provider.PresignedUrlResponse;
import com.swyp.futsal.util.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    @PostMapping("")
    public ApiResponse<TeamResponse> createTeam(
            Authentication authentication,
            @Valid @RequestBody CreateTeamRequest request) {
        User user = (User) authentication.getPrincipal();
        Team team = teamService.createTeam(user, request);
        return ApiResponse.success(new TeamResponse(team));
    }

    @GetMapping("/check-nickname")
    public ApiResponse<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean isUnique = teamService.isNameUnique(nickname);
        return ApiResponse.success(Map.of("unique", isUnique));
    }

    @GetMapping("/{teamId}/logo-presigned-url")
    public ApiResponse<PresignedUrlResponse> getLogoPresignedUrl(@PathVariable String teamId) {
        return ApiResponse.success(teamService.getLogoPresignedUrl(teamId));
    }

    @PatchMapping("{teamId}/logo")
    public ApiResponse<Optional<PresignedUrlResponse>> updateTeamLogo(
            @PathVariable String teamId,
            @RequestBody Map<String, String> request) {
        return ApiResponse.success(teamService.updateTeamLogoById(teamId, request.get("uri")));
    }

    @GetMapping("")
    public ApiResponse<List<TeamResponse>> searchTeams(@RequestParam String name) {
        List<Team> teams = teamService.searchTeams(name);
        List<TeamResponse> responses = teams.stream()
                .map(TeamResponse::new)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }
}