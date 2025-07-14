package com.itplace.userapi.security.exception;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;

@Getter
public class DuplicateEmailException extends RuntimeException {

    private final BaseCode code;

    public DuplicateEmailException(BaseCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
