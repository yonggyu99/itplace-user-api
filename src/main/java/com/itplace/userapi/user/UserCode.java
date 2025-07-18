package com.itplace.userapi.user;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserCode implements BaseCode {
    USER_INFO_SUCCESS("USER_INFO_SUCCESS", HttpStatus.OK, "사용자 정보 조회에 성공했습니다."),
    EMAIL_FIND_SUCCESS("EMAIL_FIND_SUCCESS", HttpStatus.OK, "이메일 찾기를 성공했습니다."),
    EMAIL_FIND_FAILURE("EMAIL_FIND_FAILURE", HttpStatus.BAD_REQUEST, "이메일 찾기를 실패했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
