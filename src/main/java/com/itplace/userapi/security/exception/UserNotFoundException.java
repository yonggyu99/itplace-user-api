package com.itplace.userapi.security.exception;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private final BaseCode code;

    public UserNotFoundException(BaseCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
