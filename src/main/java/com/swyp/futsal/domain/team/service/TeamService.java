package com.swyp.futsal.domain.team.service;

import com.swyp.futsal.api.team.dto.CreateTeamRequest;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.repository.TeamRepository;
import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.provider.S3Provider;
import com.swyp.futsal.provider.PresignedUrlResponse;
import com.swyp.futsal.exception.BusinessException;
import com.swyp.futsal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final S3Provider s3Provider;

    @Transactional
    public Team createTeam(User user, CreateTeamRequest request) {
        Team team = Team.builder()
                .user(user)
                .name(request.getName())
                .introduction(request.getIntroduction())
                .rule(request.getRule())
                .access(request.getAccess())
                .dues(request.getDues())
                .build();

        return teamRepository.save(team);
    }

    public boolean isNameUnique(String name) {
        return !teamRepository.existsTeamByName(name);
    }

    public PresignedUrlResponse getLogoPresignedUrl(String teamId) {
        String path = String.format("teams/%s/logo", teamId);
        return s3Provider.getUploadPresignedUrl(path);
    }

    @Transactional
    public Optional<PresignedUrlResponse> updateTeamLogoById(String teamId, String logoUri) {
        try {
            teamRepository.updateLogoById(teamId, logoUri);
            return s3Provider.getDownloadPresignedUrl(logoUri);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Team> searchTeams(String name) {
        return teamRepository.findTeamsByNameContaining(name);
    }
}