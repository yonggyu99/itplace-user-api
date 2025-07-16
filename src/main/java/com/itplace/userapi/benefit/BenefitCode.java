package com.itplace.userapi.benefit;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BenefitCode implements BaseCode {
    BENEFIT_LIST_SUCCESS("BENEFIT_LIST_SUCCESS", HttpStatus.OK, "혜택 목록 조회에 성공했습니다."),
    BENEFIT_DETAIL_SUCCESS("BENEFIT_DETAIL_SUCCESS", HttpStatus.OK, "혜택 상세 조회에 성공했습니다."),
    BENEFIT_NOT_FOUND("BENEFIT_NOT_FOUND", HttpStatus.NOT_FOUND, "존재하지 않는 혜택입니다."),
    BENEFIT_TYPE_NOT_FOUND("BENEFIT_TYPE_NOT_FOUND", HttpStatus.BAD_REQUEST, "존재하지 않는 benefit type입니다."),
    MAIN_CATEGORY_NOT_FOUND("MAIN_CATEGORY_NOT_FOUND", HttpStatus.BAD_REQUEST, "존재하지 않는 main category입니다."),
    USAGE_TYPE_NOT_FOUND("USAGE_TYPE_NOT_FOUND", HttpStatus.BAD_REQUEST, "존재하지 않는 usage type입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
