package com.itplace.userapi.benefit;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum  BenefitCode implements BaseCode {
    BENEFIT_LIST_SUCCESS("BENEFIT_LIST_SUCCESS", HttpStatus.OK, "혜택 목록 조회에 성공했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
