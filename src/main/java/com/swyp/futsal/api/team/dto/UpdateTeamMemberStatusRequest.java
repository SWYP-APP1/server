package com.swyp.futsal.api.team.dto;

import com.swyp.futsal.domain.common.enums.MemberStatus;

import lombok.Getter;

@Getter
public class UpdateTeamMemberStatusRequest {
    private MemberStatus status;
}
