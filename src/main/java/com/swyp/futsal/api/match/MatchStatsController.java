package com.swyp.futsal.api.match;

import com.swyp.futsal.api.match.dto.*;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.domain.match.service.MatchStatsService;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/match-stats")
public class MatchStatsController {
    private final AuthService authService;
    private final MatchStatsService matchStatsService;

    @GetMapping
    public ApiResponse<MatchStatsListResponse> getMatchStats(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(name = "match-id") String matchId) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchStatsService.getMatchStats(userId, matchId));
    }

    @PostMapping
    public ApiResponse<MatchStatsResponse> createMatchStats(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody MatchStatsCreateRequest request) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchStatsService.createMatchStats(userId, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteMatchStats(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String id) {
        String userId = getUserIdByHeader(authorization);
        matchStatsService.deleteMatchStats(userId, id);
        return ApiResponse.success(null);
    }

    

    private String getUserIdByHeader(String authorization) {
        String token = RequestUtil.getAuthorizationToken(authorization);
        return authService.getUserId(token);
    }
} 