package com.swyp.futsal.api.match;

import com.swyp.futsal.api.match.dto.MatchCreateRequest;
import com.swyp.futsal.api.match.dto.MatchResponse;
import com.swyp.futsal.api.match.dto.MatchRoundsUpdateRequest;
import com.swyp.futsal.api.match.dto.MatchUpdateRequest;
import com.swyp.futsal.api.match.dto.MatchDetailResponse;
import com.swyp.futsal.domain.auth.AuthService;
import com.swyp.futsal.domain.match.service.MatchService;
import com.swyp.futsal.domain.match.service.VoteService;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import com.swyp.futsal.security.util.RequestUtil;
import com.swyp.futsal.util.api.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {
    private final AuthService authService;
    private final MatchService matchService;
    private final VoteService voteService;

    @GetMapping
    public ApiResponse<List<MatchResponse>> getMatches(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam String teamId
            ) {

        String userId = getUserIdByHeader(authorization);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return ApiResponse.success(matchService.getMatches(userId, teamId, pageRequest));
    }

    @GetMapping("/recent")
    public ApiResponse<Optional<MatchResponse>> getRecentMatch(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String teamId) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchService.getRecentMatch(userId, teamId));
    }

    @GetMapping("/vote")
    public ApiResponse<List<MatchResponse>> getVoteSchedule(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String date) {
                
        String userId = getUserIdByHeader(authorization);
        try{
            YearMonth yearMonth = YearMonth.parse(date);
            return ApiResponse.success(voteService.getVoteSchedule(userId, date));
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorCode.BAD_REQUEST_INVALID_PARAMETER_FORMAT);
        }
    }

    @PostMapping
    public ApiResponse<MatchResponse> createMatch(@RequestHeader("Authorization") String authorization, @Valid @RequestBody MatchCreateRequest request) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchService.createMatch(userId, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteMatch(@RequestHeader("Authorization") String authorization, @PathVariable String id) {
        String userId = getUserIdByHeader(authorization);
        matchService.deleteMatch(id, userId);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/rounds")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> updateRounds(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String id,
            @Valid @RequestBody MatchRoundsUpdateRequest request) {
        String userId = getUserIdByHeader(authorization);
        matchService.updateRounds(userId, id, request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}")
    public ApiResponse<MatchResponse> updateMatch(@RequestHeader("Authorization") String authorization, @PathVariable String id, @Valid @RequestBody MatchUpdateRequest request) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchService.updateAllById(userId, id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<MatchDetailResponse> getMatchDetail(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String id) {
        String userId = getUserIdByHeader(authorization);
        return ApiResponse.success(matchService.getMatchDetail(userId, id));
    }

    @PatchMapping("/{id}/vote-status/ended")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> updateVoteStatusToEnded(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String id) {
        String userId = getUserIdByHeader(authorization);
        matchService.updateVoteStatusToEnded(userId, id);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/status/ongoing")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> updateStatusToOngoing(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String id) {
        String userId = getUserIdByHeader(authorization);
        matchService.updateStatusToOngoing(userId, id);
        return ApiResponse.success(null);
    }

    private String getUserIdByHeader(String authorization) {
        String token = RequestUtil.getAuthorizationToken(authorization);
        return authService.getUserId(token);
    }
} 