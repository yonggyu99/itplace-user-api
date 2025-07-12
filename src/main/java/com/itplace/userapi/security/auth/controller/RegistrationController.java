package com.itplace.userapi.security.auth.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.common.BaseCode;
import com.itplace.userapi.security.auth.dto.request.SignUpRequest;
import com.itplace.userapi.security.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ApiResponse<Void> signUp(@RequestBody @Validated SignUpRequest request) {
        authService.signUp(request);
        return ApiResponse.ok(BaseCode.SIGNUP_SUCCESS);
    }
}
