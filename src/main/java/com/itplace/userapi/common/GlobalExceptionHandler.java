package com.itplace.userapi.common;

import com.itplace.userapi.common.exception.BusinessException;
import com.itplace.userapi.security.SecurityCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalStateException(IllegalStateException ex) {
        ApiResponse<Void> body = ApiResponse.of(SecurityCode.Test, null);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(IllegalStateException ex) {
        ApiResponse<Void> body = ApiResponse.of(SecurityCode.Test, null);
        return new ResponseEntity<>(body, body.getStatus());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        ApiResponse<Void> body = ApiResponse.of(ex.getCode(), null);
        return new ResponseEntity<>(body, body.getStatus());
    }
}