package com.itplace.userapi.history;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MembershipHistoryCode implements BaseCode {
    MEMBERSHIP_HISTORY_SUCCESS("MEMBERSHIP_HISTORY_SUCCESS", HttpStatus.OK, "멤버십 사용 내역 조회에 성공했습니다."),
    MEMBERSHIP_HISTORY_SUMMARY_SUCCESS("MEMBERSHIP_HISTORY_SUMMARY_SUCCESS", HttpStatus.OK, "이번 달 할인 금액 조회에 성공했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}

