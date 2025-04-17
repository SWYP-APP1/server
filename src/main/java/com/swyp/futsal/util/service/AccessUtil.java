package com.swyp.futsal.util.service;

import java.util.Optional;

import com.swyp.futsal.domain.common.enums.TeamRole;

public class AccessUtil {
    public static boolean hasRequiredRole(String requestedTeamMemberId, Optional<String> substituteTeamMemberId, TeamRole memberRole, TeamRole requiredRole) {
        if (substituteTeamMemberId == null) {
            substituteTeamMemberId = Optional.empty();
        }
        
        if (substituteTeamMemberId.isPresent() && substituteTeamMemberId.get().equals(requestedTeamMemberId)) {
            return true;
        }

        return switch (requiredRole) {
            case OWNER -> memberRole == TeamRole.OWNER;
            case TEAM_LEADER -> memberRole == TeamRole.OWNER || memberRole == TeamRole.TEAM_LEADER;
            case TEAM_DEPUTY_LEADER -> memberRole == TeamRole.OWNER || memberRole == TeamRole.TEAM_LEADER 
                    || memberRole == TeamRole.TEAM_DEPUTY_LEADER;
            case TEAM_SECRETARY -> memberRole == TeamRole.OWNER || memberRole == TeamRole.TEAM_DEPUTY_LEADER 
                    || memberRole == TeamRole.TEAM_SECRETARY;
            case TEAM_MEMBER -> true;
        };
    }
}
