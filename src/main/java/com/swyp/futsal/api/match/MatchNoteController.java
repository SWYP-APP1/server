package com.swyp.futsal.api.match;

import com.swyp.futsal.api.match.dto.*;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.domain.match.service.MatchNoteService;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/match-notes")
public class MatchNoteController {
    private final AuthService authService;
    private final MatchNoteService matchNoteService;

    @GetMapping("/presigned-url")
    public ApiResponse<MatchNotePresignedUrlResponse> getPresignedUrl(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(name = "match-id") String matchId) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchNoteService.getPresignedUrl(userId, matchId));
    }

    @PutMapping
    public ApiResponse<MatchNoteResponse> upsertMatchNote(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody MatchNoteRequest request) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchNoteService.upsertMatchNote(userId, request));
    }

    @GetMapping("/one")
    public ApiResponse<MatchNoteResponse> getMatchNote(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(name = "match-id") String matchId) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchNoteService.getMatchNote(userId, matchId));
    }

    private String getUserIdByHeader(String authorization) {
        String token = RequestUtil.getAuthorizationToken(authorization);
        return authService.getUserId(token);
    }
} 