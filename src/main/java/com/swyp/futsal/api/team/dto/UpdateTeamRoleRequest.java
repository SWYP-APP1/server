package com.swyp.futsal.api.team.dto;

import com.swyp.futsal.domain.common.enums.TeamRole;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateTeamRoleRequest {

  @NotNull
  @Enumerated(EnumType.STRING)
  private TeamRole role;
}
