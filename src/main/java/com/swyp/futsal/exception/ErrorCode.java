package com.swyp.futsal.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400 BAD REQUEST
    BAD_REQUEST_REQUIRED_PARAMETER_MISSING("PARAM-001", HttpStatus.BAD_REQUEST,
            "BAD_REQUEST_REQUIRED_PARAMETER_MISSING"),
    BAD_REQUEST_INVALID_PARAMETER_FORMAT("PARAM-002", HttpStatus.BAD_REQUEST, "BAD_REQUEST_INVALID_PARAMETER_FORMAT"),
    BAD_REQUEST_INVALID_PARAMETER_VALUE("PARAM-003", HttpStatus.BAD_REQUEST, "BAD_REQUEST_INVALID_PARAMETER_VALUE"),
    BAD_REQUEST_INVALID_PAGE_OR_SIZE_VALUE("PARAM-004", HttpStatus.BAD_REQUEST,
            "BAD_REQUEST_INVALID_PAGE_OR_SIZE_VALUE"),

    // 401 UNAUTHORIZED
    UNAUTHORIZED_TOKEN_AUTHENTICATION_FAILED("AUTH-001", HttpStatus.UNAUTHORIZED,
            "UNAUTHORIZED_TOKEN_AUTHENTICATION_FAILED"),
    UNAUTHORIZED_REFRESH_TOKEN_HAS_EXPIRED("AUTH-002", HttpStatus.UNAUTHORIZED,
            "UNAUTHORIZED_REFRESH_TOKEN_HAS_EXPIRED"),
    UNAUTHORIZED_ACCESS_TOKEN_INVALID("AUTH-003", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ACCESS_TOOKEN_INVALID"),

    // 403 FORBIDDEN
    FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED("ROLE-001", HttpStatus.FORBIDDEN,
            "FORBIDDEN_TEAM_LEADER_PERMISSION_REQUIRED"),
    FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED("ROLE-002", HttpStatus.FORBIDDEN, "FORBIDDEN_ONLY_TEAM_MEMBER_REQUIRED"),

    // 404 NOT FOUND
    NOT_FOUND_TEAM_ID("TEAM-001", HttpStatus.NOT_FOUND, "NOT_FOUND_TEAM_ID"),

    // 409 CONFLICT
    CONFLICT_NICKNAME_ALREADY_EXISTS("USER-001", HttpStatus.CONFLICT, "CONFLICT_NICKNAME_ALREADY_EXISTS"),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR("SERVER-001", HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}