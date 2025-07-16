package com.itplace.userapi.map.exception;

import com.itplace.userapi.common.BaseCode;
import lombok.Getter;

@Getter
public class StoreKeywordException extends RuntimeException {
    private final BaseCode code;

    public StoreKeywordException(BaseCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
