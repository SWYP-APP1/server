package com.swyp.futsal.api.match;

import com.swyp.futsal.api.match.dto.*;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.domain.common.enums.VoteType;
import com.swyp.futsal.domain.match.service.VoteService;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/votes")
public class VoteController {
    private final AuthService authService;
    private final VoteService voteService;

    @GetMapping
    public ApiResponse<VoteListResponse> getVotes(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(name = "match-id") String matchId,
            @RequestParam VoteType type) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(voteService.getVotes(userId, matchId, type));
    }

    @PostMapping("/mom")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> voteMom(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody VoteMomRequest request) {
        String userId = getUserIdByHeader(authorization);
        voteService.voteMom(userId, request);
        return ApiResponse.success(null);
    }

    @PutMapping("/participation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> voteParticipation(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody VoteParticipationRequest request) {
        String userId = getUserIdByHeader(authorization);
        voteService.voteParticipation(userId, request);
        return ApiResponse.success(null);
    }

    

    private String getUserIdByHeader(String authorization) {
        String token = RequestUtil.getAuthorizationToken(authorization);
        return authService.getUserId(token);
    }
} 