package com.itplace.userapi.map;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StoreCode implements BaseCode {
    STORE_LIST_SUCCESS("STORE_LIST_SUCCESS", HttpStatus.OK, "사용자 위치 기반 매장 조회에 성공했습니다."),
    KEYWORD_REQUIRED("KEYWORD_REQUIRED", HttpStatus.BAD_REQUEST, "검색어를 입력하세요"),

    STORE_NOT_FOUND("STORE_NOT_FOUND", HttpStatus.NOT_FOUND, "지점을 찾을 수 없습니다."),
    STORE_PARTNER_MISMATCH("STORE_PARTNER_MISMATCH", HttpStatus.BAD_REQUEST, "지점과 제휴사가 일치하지 않습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
