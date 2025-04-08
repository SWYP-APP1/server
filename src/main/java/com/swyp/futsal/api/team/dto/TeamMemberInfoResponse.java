package com.swyp.futsal.api.team.dto;

import com.swyp.futsal.domain.common.enums.Gender;
import com.swyp.futsal.domain.common.enums.MemberStatus;
import com.swyp.futsal.domain.common.enums.TeamRole;
import com.swyp.futsal.domain.team.entity.Team;
import com.swyp.futsal.domain.user.entity.User;
import java.util.List;
import lombok.Getter;

@Getter
public class TeamMemberInfoResponse {
  private final String teamId;
  private final String teamName;
  private final String logoUrl;
  private final List<MemberInfo> members;

  public TeamMemberInfoResponse(Team team, List<MemberInfo> members) {
    this.teamId = team.getId();
    this.teamName = team.getName();
    this.logoUrl = team.getLogoUri();
    this.members = members;
  }

  @Getter
  public static class MemberInfo {
    private final String userId;
    private final TeamRole teamRole;
    private final String userName;
    private final String profileUri;
    private final Gender gender;
    private final String birthDate;
    private final MemberStatus status;

    public MemberInfo(User user, MemberStatus status, TeamRole teamRole) {
      this.userId = user.getId();
      this.userName = user.getName();
      this.profileUri = user.getProfileUri();
      this.gender = user.getGender();
      this.birthDate = user.getBirthDate();
      this.status = status;
      this.teamRole = teamRole;
    }
  }
}
