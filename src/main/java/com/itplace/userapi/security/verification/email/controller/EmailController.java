package com.itplace.userapi.security.verification.email.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.verification.email.dto.EmailConfirmRequest;
import com.itplace.userapi.security.verification.email.dto.EmailVerificationRequest;
import com.itplace.userapi.security.verification.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/verification")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ApiResponse<Void> send(@RequestBody @Validated EmailVerificationRequest request) {
        emailService.send(request);
        return ApiResponse.ok(SecurityCode.EMAIL_SEND_SUCCESS);
    }

    @PostMapping("/email/confirm")
    public ApiResponse<Void> confirm(@RequestBody @Validated EmailConfirmRequest request) {
        emailService.confirm(request);
        return ApiResponse.ok(SecurityCode.EMAIL_VERIFICATION_SUCCESS);
    }
}
