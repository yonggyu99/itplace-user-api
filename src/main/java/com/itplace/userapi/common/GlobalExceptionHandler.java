package com.itplace.userapi.common;

import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.exception.EmailVerificationException;
import com.itplace.userapi.security.exception.SmsVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public ApiResponse<?> handleIllegalStateException(IllegalStateException ex) {
        return ApiResponse.of(SecurityCode.Test, null);
    }

    @ExceptionHandler(SmsVerificationException.class)
    public ApiResponse<?> handleSmsVerificationException(SmsVerificationException ex) {
        return ApiResponse.of(ex.getCode(), null);
    }

    @ExceptionHandler(EmailVerificationException.class)
    public ApiResponse<?> handleEmailVerificationException(EmailVerificationException ex) {
        return ApiResponse.of(ex.getCode(), null);
    }
}