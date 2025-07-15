package com.itplace.userapi.security;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SecurityCode implements BaseCode {

    Test("Test", HttpStatus.OK, "Test"),

    INVALID_REGISTRATION_SESSION("INVALID_REGISTRATION_SESSION", HttpStatus.BAD_REQUEST, "잘못된 가입 요청입니다."),

    LOGIN_SUCCESS("LOGIN_SUCCESS", HttpStatus.OK, "성공적으로 로그인 되었습니다."),

    LOGOUT_SUCCESS("LOGOUT_SUCCESS", HttpStatus.OK, "성공적으로 로그아웃 되었습니다."),

    SIGNUP_SUCCESS("SIGNUP_SUCCESS", HttpStatus.OK, "성공적으로 회원가입 되었습니다."),

    LINK_LOCAL_SUCCESS("LINK_LOCAL_SUCCESS", HttpStatus.OK, "성공적으로 계정이 연동 되었습니다."),

    RENEW_ACCESS_TOKEN("RENEW_ACCESS_TOKEN", HttpStatus.OK, "액세스 토큰이 갱신되었습니다."),

    INVALID_INPUT_VALUE("INVALID_INPUT_VALUE", HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    REFRESH_TOKEN_REQUIRE("REFRESH_TOKEN_REQUIRE", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 필요합니다."),

    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

    INVALID_TOKEN_TYPE("INVALID_TOKEN_TYPE", HttpStatus.UNAUTHORIZED, "잘못된 토큰 타입입니다."),

    INVALID_TOKEN("INVALID_TOKEN", HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 오류가 발생했습니다"),

    SMS_VERIFICATION_SUCCESS("SMS_VERIFICATION_SUCCESS", HttpStatus.OK, "문자 인증에 성공했습니다."),

    SMS_CODE_EXPIRED("SMS_CODE_EXPIRED", HttpStatus.BAD_REQUEST, "문자 인증 코드가 만료되었습니다."),

    SMS_CODE_MISMATCH("SMS_CODE_MISMATCH", HttpStatus.BAD_REQUEST, "문자 인증 코드가 일치하지 않습니다."),

    SMS_SEND_SUCCESS("SMS_SEND_SUCCESS", HttpStatus.OK, "문자 인증 코드 발송에 성공했습니다."),

    SMS_SEND_FAILURE("SMS_SEND_FAILURE", HttpStatus.INTERNAL_SERVER_ERROR, "문자 인증 코드 발송에 실패했습니다."),

    UPLUS_DATA_FOUND("UPLUS_DATA_FOUND", HttpStatus.OK, "Uplus 데이터를 성공적으로 불러왔습니다."),

    UPLUS_DATA_NOT_FOUND("UPLUS_DATA_NOT_FOUND", HttpStatus.BAD_REQUEST, "Uplus 데이터를 불러오지 못했습니다."),

    EMAIL_VERIFICATION_SUCCESS("EMAIL_VERIFICATION_SUCCESS", HttpStatus.OK, "이메일 인증에 성공했습니다."),

    EMAIL_CODE_EXPIRED("EMAIL_CODE_EXPIRED", HttpStatus.BAD_REQUEST, "이메일 인증 코드가 만료되었습니다."),

    EMAIL_CODE_MISMATCH("EMAIL_CODE_MISMATCH", HttpStatus.BAD_REQUEST, "이메일 인증 코드가 일치하지 않습니다."),

    EMAIL_SEND_SUCCESS("EMAIL_SEND_SUCCESS", HttpStatus.OK, "이메일 인증 코드 발송에 성공했습니다."),

    EMAIL_SEND_FAILURE("EMAIL_SEND_FAILURE", HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다."),

    DUPLICATE_EMAIL("DUPLICATE_EMAIL", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),

    DUPLICATE_PHONE_NUMBER("DUPLICATE_PHONE_NUMBER", HttpStatus.CONFLICT, "이미 사용 중인 전화번호입니다."),

    PASSWORD_MISMATCH("PASSWORD_MISMATCH", HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    SMS_VERIFICATION_NOT_COMPLETED("SMS_VERIFICATION_NOT_COMPLETED", HttpStatus.BAD_REQUEST, "SMS 인증이 완료되지 않았습니다."),

    EMAIL_VERIFICATION_NOT_COMPLETED("EMAIL_VERIFICATION_NOT_COMPLETED", HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),

    MISMATCHED_VERIFIED_DATA("MISMATCHED_VERIFIED_DATA", HttpStatus.BAD_REQUEST, "인증된 정보와 일치하지 않습니다."),

    USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다"),

    // OAuth2 로그인 시 추가 정보가 필요함을 나타내는 코드
    SIGNUP_REQUIRED("SIGNUP_REQUIRED", HttpStatus.OK, "회원가입이 필요합니다. 추가 정보를 입력해주세요.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    public static BaseCode fromCode(String code) {
        for (SecurityCode securityCode : SecurityCode.values()) {
            if (securityCode.getCode().equals(code)) {
                return securityCode;
            }
        }
        throw new IllegalArgumentException("No matching SecurityCode for code: " + code);
    }
}
