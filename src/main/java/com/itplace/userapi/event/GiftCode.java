package com.itplace.userapi.event;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GiftCode implements BaseCode {
    SCRATCH_SUCCESS("SCRATCH_SUCCESS", HttpStatus.OK, "복권을 성공적으로 긁었습니다."),
    SCRATCH_FAIL("SCRATCH_FAIL", HttpStatus.OK, "꽝입니다. 다음 기회를 노려보세요!"),
    COUPON_LACK("COUPON_LACK", HttpStatus.BAD_REQUEST, "별이 부족합니다."),
    GIFT_EMPTY("GIFT_EMPTY", HttpStatus.OK, "모든 경품이 소진되었습니다."),
    GIFT_LIST("GIFT_LIST", HttpStatus.OK, "경품 목록를 불러왔습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
