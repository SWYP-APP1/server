package com.swyp.futsal.api.match;

import com.swyp.futsal.api.match.dto.*;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.domain.match.service.MatchParticipantService;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/match-participants")
public class MatchParticipantController {
    private final AuthService authService;
    private final MatchParticipantService matchParticipantService;

    @GetMapping
    public ApiResponse<MatchParticipantListResponse> getMatchParticipants(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String matchId) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchParticipantService.getMatchParticipants(userId, matchId));
    }

    @PostMapping
    public ApiResponse<CreateMatchParticipantResponse> registerParticipants(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody MatchParticipantRequest request
        ) {

        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchParticipantService.registerParticipants(userId, request));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteParticipants(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody MatchParticipantDeleteRequest request
        ) {

        String userId = getUserIdByHeader(authorization);
        matchParticipantService.deleteParticipants(userId, request.getMatchId(), request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/bulk/sub-team")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateSubTeam(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody SubTeamUpdateRequest request
        ) {

        String userId = getUserIdByHeader(authorization);
        matchParticipantService.updateSubTeam(userId, request.getMatchId(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mom")
    public ApiResponse<MatchParticipantMomResponse> getMomCandidates(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String matchId) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchParticipantService.getMomCandidates(userId, matchId));
    }

    private String getUserIdByHeader(String authorization) {
        String token = RequestUtil.getAuthorizationToken(authorization);
        return authService.getUserId(token);
    }
}
