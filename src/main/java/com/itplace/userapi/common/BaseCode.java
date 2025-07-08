package com.itplace.userapi.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BaseCode {

    TEST("TEST", HttpStatus.OK, "TEST"),
    RENEW_ACCESS_TOKEN("RENEW_ACCESS_TOKEN_200", HttpStatus.OK, "액세스 토큰이 갱신되었습니다."),
    REFRESH_TOKEN_REQUIRE("REFRESH_TOKEN_REQUIRE_401", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 필요합니다."),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED_401", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
    INVALID_TOKEN_TYPE("INVALID_TOKEN_TYPE_401", HttpStatus.UNAUTHORIZED, "잘못된 토큰 타입입니다."),
    INVALID_TOKEN("INVALID_TOKEN_401", HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR_500", HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 오류가 발생했습니다"),
    SMS_CODE_INCORRECT("SMS_CODE_INCORRECT_400", HttpStatus.BAD_REQUEST, "올바른 문자 인증 코드가 아닙니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
