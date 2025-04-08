package com.swyp.futsal.api.team.dto;

import com.swyp.futsal.domain.common.enums.TeamRole;
import lombok.Getter;

@Getter
public class TeamRoleRequest {

  private String role;

  public TeamRole getRole() {
    return TeamRole.valueOf(role.toUpperCase());
  }
}
