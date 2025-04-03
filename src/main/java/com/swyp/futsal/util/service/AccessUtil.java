package com.swyp.futsal.util.service;

import com.swyp.futsal.domain.common.enums.TeamRole;

public class AccessUtil {
    public static boolean hasRequiredRole(TeamRole memberRole, TeamRole requiredRole) {
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
