package com.itplace.userapi.security.verification.sms.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.verification.sms.dto.SmsConfirmRequest;
import com.itplace.userapi.security.verification.sms.dto.SmsConfirmResponse;
import com.itplace.userapi.security.verification.sms.dto.SmsVerificationRequest;
import com.itplace.userapi.security.verification.sms.dto.SmsVerificationResponse;
import com.itplace.userapi.security.verification.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/verification")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/sms")
    public ApiResponse<SmsVerificationResponse> send(@RequestBody @Validated SmsVerificationRequest request) {
        SmsVerificationResponse data = smsService.send(request);
        return ApiResponse.of(SecurityCode.SMS_SEND_SUCCESS, data);
    }

    @PostMapping("/sms/confirm")
    public ApiResponse<SmsConfirmResponse> confirm(@RequestBody @Validated SmsConfirmRequest request) {
        SmsConfirmResponse data = smsService.confirm(request);
        return ApiResponse.of(SecurityCode.SMS_VERIFICATION_SUCCESS, data);
    }
}
