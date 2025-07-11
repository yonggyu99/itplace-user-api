package com.itplace.userapi.common.exception;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;

@Getter
public class SmsVerificationException extends RuntimeException {

    private final BaseCode code;

    public SmsVerificationException(BaseCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
