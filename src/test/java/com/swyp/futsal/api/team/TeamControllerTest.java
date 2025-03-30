package com.swyp.futsal.api.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.futsal.api.team.dto.CreateTeamRequest;
import com.swyp.futsal.api.team.dto.TeamResponse;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.team.service.TeamService;
import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.provider.PresignedUrlResponse;
import com.swyp.futsal.util.api.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TeamControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private ObjectMapper objectMapper;
    private User testUser;
    private Team testTeam;
    private CreateTeamRequest createTeamRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
        objectMapper = new ObjectMapper();

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

    // @Test
    // @WithMockUser
    // void createTeam_ShouldCreateTeam() throws Exception {
    // // given
    // given(teamService.createTeam(any(User.class), any(CreateTeamRequest.class)))
    // .willReturn(testTeam);

    // // when & then
    // mockMvc.perform(post("/teams")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(objectMapper.writeValueAsString(createTeamRequest)))
    // .andExpect(status().isOk())
    // .andExpect(jsonPath("$.success").value(true))
    // .andExpect(jsonPath("$.data.name").value("Test Team"))
    // .andExpect(jsonPath("$.data.id").value("test-team-id"));
    // }

    @Test
    @WithMockUser
    void createTeam_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // given
        CreateTeamRequest invalidRequest = new CreateTeamRequest();
        // name is required but not set

        // when & then
        mockMvc.perform(post("/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkNickname_WhenNameExists_ShouldReturnFalse() throws Exception {
        // given
        given(teamService.isNameUnique("Existing Team")).willReturn(false);

        // when & then
        mockMvc.perform(get("/teams/check-nickname")
                .param("nickname", "Existing Team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unique").value(false));
    }

    @Test
    void checkNickname_WhenNameDoesNotExist_ShouldReturnTrue() throws Exception {
        // given
        given(teamService.isNameUnique("New Team")).willReturn(true);

        // when & then
        mockMvc.perform(get("/teams/check-nickname")
                .param("nickname", "New Team"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unique").value(true));
    }

    @Test
    @WithMockUser
    void getLogoPresignedUrl_ShouldReturnUrl() throws Exception {
        // given
        String teamId = "test";
        PresignedUrlResponse response = new PresignedUrlResponse(
                "https://example.com/upload", "teams/logo/test.jpg");
        given(teamService.getLogoPresignedUrl(teamId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/teams/logo-presigned-url"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.url").value("https://example.com/upload"))
                .andExpect(jsonPath("$.data.uri").value("teams/logo/test.jpg"));
    }

    @Test
    @WithMockUser
    void searchTeams_ShouldReturnTeams() throws Exception {
        // given
        List<Team> teams = Arrays.asList(testTeam);
        given(teamService.searchTeams("Test")).willReturn(teams);

        // when & then
        mockMvc.perform(get("/teams")
                .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Test Team"))
                .andExpect(jsonPath("$.data[0].id").value("test-team-id"));
    }
}