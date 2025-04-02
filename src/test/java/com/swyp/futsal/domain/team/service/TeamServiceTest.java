package com.swyp.futsal.domain.team.service;

import com.swyp.futsal.api.team.dto.CreateTeamRequest;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.repository.TeamRepository;
import com.swyp.futsal.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @InjectMocks
    private TeamService teamService;

    @Mock
    private TeamRepository teamRepository;

    private User testUser;
    private Team testTeam;
    private CreateTeamRequest createTeamRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("test-user-id")
                .email("test@example.com")
                .uid("test-uid")
                .build();

        testTeam = Team.builder()
                .id("test-team-id")
                .name("Test Team")
                .introduction("Test Introduction")
                .rule("Test Rule")
                .access(TeamRole.OWNER)
                .dues(10000)
                .user(testUser)
                .build();

        createTeamRequest = new CreateTeamRequest();
        createTeamRequest.setName("Test Team");
        createTeamRequest.setIntroduction("Test Introduction");
        createTeamRequest.setRule("Test Rule");
        createTeamRequest.setAccess(TeamRole.OWNER);
        createTeamRequest.setDues(10000);
    }

    @Test
    void createTeam_ShouldCreateTeam() {
        // given
        given(teamRepository.save(any(Team.class))).willReturn(testTeam);

        // when
        Team result = teamService.createTeam(testUser.getId(), createTeamRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(createTeamRequest.getName());
        assertThat(result.getUser()).isEqualTo(testUser);
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void isNameUnique_WhenNameExists_ShouldReturnFalse() {
        // given
        given(teamRepository.existsTeamByName("Existing Team")).willReturn(true);

        // when
        boolean result = teamService.isNameUnique("Existing Team");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void isNameUnique_WhenNameDoesNotExist_ShouldReturnTrue() {
        // given
        given(teamRepository.existsTeamByName("New Team")).willReturn(false);

        // when
        boolean result = teamService.isNameUnique("New Team");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void searchTeams_ShouldReturnTeams() {
        // given
        List<Team> expectedTeams = Arrays.asList(testTeam);
        given(teamRepository.findTeamsByNameContaining("Test")).willReturn(expectedTeams);

        // when
        List<Team> result = teamService.searchTeams("Test");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Team");
    }
} 