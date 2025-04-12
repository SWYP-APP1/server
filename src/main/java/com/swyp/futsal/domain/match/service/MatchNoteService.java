package com.swyp.futsal.domain.match.service;

import com.querydsl.core.Tuple;
import com.swyp.futsal.api.match.dto.*;
import com.swyp.futsal.domain.match.entity.Match;
import com.swyp.futsal.domain.match.entity.MatchNote;
import com.swyp.futsal.domain.match.repository.MatchNoteRepository;
import com.swyp.futsal.domain.match.repository.MatchRepository;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.entity.TeamMember;
import com.swyp.futsal.domain.team.repository.TeamMemberRepository;
import com.swyp.futsal.domain.user.repository.UserRepository;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import com.swyp.futsal.provider.PresignedUrlResponse;
import com.swyp.futsal.provider.S3Provider;
import com.swyp.futsal.util.service.AccessUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchNoteService {
    private final Logger logger = LoggerFactory.getLogger(MatchNoteService.class);
    private final MatchNoteRepository matchNoteRepository;
    private final MatchRepository matchRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final S3Provider s3Provider;

    @Transactional(readOnly = true)
    public MatchNotePresignedUrlResponse getPresignedUrl(String userId, String matchId) {
        logger.info("Get presigned url: userId={}, matchId={}", userId, matchId);
        validateTeamManagerRole(userId, matchId);
        
        String uri = String.format("match-notes/%s", matchId);
        PresignedUrlResponse presignedUrl = s3Provider.getUploadPresignedUrl(uri);
        
        return MatchNotePresignedUrlResponse.builder()
                .url(presignedUrl.getUrl())
                .uri(presignedUrl.getUri())
                .build();
    }

    @Transactional
    public MatchNoteResponse upsertMatchNote(String userId, MatchNoteRequest request) {
        logger.info("Upsert match note: userId={}, request={}", userId, request);
        validateTeamManagerRole(userId, request.getMatchId());

        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_USER_ID));

        Optional<MatchNote> matchNote = matchNoteRepository.findOneByMatchId(request.getMatchId());
        if (matchNote.isPresent()) {
            logger.info("Update existing match note: userId={}, request={}", userId, request);
            // Update existing note
            matchNote.get().getPhotoUris().clear();
            matchNote.get().getPhotoUris().addAll(request.getPhotoUris());
            matchNote.get().updateDescription(request.getDescription());
            matchNoteRepository.save(matchNote.get());
            return getMatchNoteResponse(matchNote.get());
        } else {
            logger.info("Create new match note: userId={}, request={}", userId, request);
            // Create new note
            MatchNote newMatchNote = MatchNote.builder()
                    .match(match)
                    .user(user)
                    .description(request.getDescription())
                    .photoUris(request.getPhotoUris())
                    .build();
            matchNoteRepository.save(newMatchNote);
            return getMatchNoteResponse(newMatchNote);
        }
    }

    @Transactional(readOnly = true)
    public MatchNoteResponse getMatchNote(String userId, String matchId) {
        logger.info("Get match note: userId={}, matchId={}", userId, matchId);
        MatchNote matchNote = matchNoteRepository.findOneByMatchId(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_NOTE_ID));

        validateTeamManagerRole(userId, matchNote.getMatch().getId());

        return getMatchNoteResponse(matchNote);
    }

    private MatchNoteResponse getMatchNoteResponse(MatchNote matchNote) {
        logger.info("Get match note response: matchNote={}", matchNote);
        List<MatchNoteResponse.Photo> photos = new ArrayList<>();
        for (String uri : matchNote.getPhotoUris()) {
            var presignedUrl = s3Provider.getDownloadPresignedUrl(uri);
            photos.add(MatchNoteResponse.Photo.builder()
                    .url(presignedUrl.map(url -> url.getUrl()).orElse(null))
                    .uri(uri)
                    .build());
        }
        return MatchNoteResponse.from(matchNote, photos);
    }

    private void validateTeamManagerRole(String userId, String matchId) {
        logger.info("Validate team manager role: userId={}, matchId={}", userId, matchId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MATCH_ID));
        
        Optional<Tuple> tuple = teamMemberRepository.findOneWithTeamByUserAndTeamIdAndIsDeletedFalse(userId, match.getTeam().getId());
        if (tuple.isEmpty()) {
            logger.info("Team member not found: userId={}, matchId={}", userId, matchId);
            throw new BusinessException(ErrorCode.FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED);
        }

        TeamMember teamMember = tuple.get().get(0, TeamMember.class);
        Team team = tuple.get().get(1, Team.class);

        AccessUtil.hasRequiredRole(teamMember.getId(), Optional.ofNullable(match.getSubstituteTeamMemberId()), teamMember.getRole(), team.getAccess());
    }
} 