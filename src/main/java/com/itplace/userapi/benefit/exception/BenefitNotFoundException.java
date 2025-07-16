package com.itplace.userapi.benefit.exception;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;

@Getter
public class BenefitNotFoundException extends RuntimeException {
    private final BaseCode code;

    public BenefitNotFoundException(BaseCode code) {
        super(code.getMessage());
        this.code = code;
    }
}

