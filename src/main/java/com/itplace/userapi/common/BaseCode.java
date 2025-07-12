package com.itplace.userapi.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BaseCode {

    Test("Test", HttpStatus.OK, "Test"),
    INVALID_REGISTRATION_SESSION("LOGIN_SUCCESS_200", HttpStatus.OK, "성공적으로 로그인 되었습니다."),
    LOGIN_SUCCESS("LOGIN_SUCCESS_200", HttpStatus.OK, "성공적으로 로그인 되었습니다."),
    LOGOUT_SUCCESS("LOGOUT_SUCCESS_200", HttpStatus.OK, "성공적으로 로그아웃 되었습니다."),
    SIGNUP_SUCCESS("SIGNUP_SUCCESS_200", HttpStatus.OK, "성공적으로 회원가입 되었습니다."),
    RENEW_ACCESS_TOKEN("RENEW_ACCESS_TOKEN_200", HttpStatus.OK, "액세스 토큰이 갱신되었습니다."),

    INVALID_INPUT_VALUE("INVALID_INPUT_VALUE_400", HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    REFRESH_TOKEN_REQUIRE("REFRESH_TOKEN_REQUIRE_401", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 필요합니다."),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED_401", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
    INVALID_TOKEN_TYPE("INVALID_TOKEN_TYPE_401", HttpStatus.UNAUTHORIZED, "잘못된 토큰 타입입니다."),
    INVALID_TOKEN("INVALID_TOKEN_401", HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR_500", HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 오류가 발생했습니다"),
    SMS_VERIFICATION_SUCCESS("SMS_VERIFICATION_SUCCESS_200", HttpStatus.OK, "문자 인증에 성공했습니다."),

    SMS_CODE_EXPIRED("SMS_CODE_EXPIRED_400", HttpStatus.BAD_REQUEST, "문자 인증 코드가 만료되었습니다."),
    SMS_CODE_MISMATCH("SMS_CODE_MISMATCH_400", HttpStatus.BAD_REQUEST, "문자 인증 코드가 일치하지 않습니다."),

    SMS_SEND_SUCCESS("SMS_SEND_SUCCESS_200", HttpStatus.OK, "문자 인증 코드 발송에 성공했습니다."),
    UPLUS_DATA_EXISTS("UPLUS_DATA_EXISTS_200", HttpStatus.OK, "Uplus 데이터에 해당 사용자가 존재합니다."),

    EMAIL_VERIFICATION_SUCCESS("EMAIL_VERIFICATION_SUCCESS_200", HttpStatus.OK, "이메일 인증에 성공했습니다."),
    EMAIL_CODE_EXPIRED("EMAIL_CODE_EXPIRED_400", HttpStatus.BAD_REQUEST, "이메일 인증 코드가 만료되었습니다."),
    EMAIL_CODE_MISMATCH("EMAIL_CODE_MISMATCH_400", HttpStatus.BAD_REQUEST, "이메일 인증 코드가 일치하지 않습니다."),

    EMAIL_SEND_SUCCESS("EMAIL_SEND_SUCCESS_200", HttpStatus.OK, "이메일 인증 코드 발송에 성공했습니다."),

    EMAIL_SEND_FAILURE("EMAIL_SEND_FAILURE_500", HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다."),

    DUPLICATE_EMAIL("DUPLICATE_EMAIL_409", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_PHONE_NUMBER("DUPLICATE_PHONE_NUMBER_409", HttpStatus.CONFLICT, "이미 사용 중인 전화번호입니다."),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH_400", HttpStatus.BAD_REQUEST, "비밀번호를 다시 확인해주세요."),
    SMS_VERIFICATION_NOT_COMPLETED("SMS_VERIFICATION_NOT_COMPLETED_400", HttpStatus.BAD_REQUEST, "SMS 인증이 완료되지 않았습니다."),
    EMAIL_VERIFICATION_NOT_COMPLETED("EMAIL_VERIFICATION_NOT_COMPLETED_400", HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
    MISMATCHED_VERIFIED_DATA("MISMATCHED_VERIFIED_DATA_400", HttpStatus.BAD_REQUEST, "인증된 정보와 일치하지 않습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
