package com.itplace.userapi.security.verification.sms.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.common.BaseCode;
import com.itplace.userapi.security.verification.sms.dto.SmsConfirmRequest;
import com.itplace.userapi.security.verification.sms.dto.SmsVerificationRequest;
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
    public ApiResponse<Void> send(@RequestBody @Validated SmsVerificationRequest request) {
        smsService.send(request.getPhoneNumber());
        return ApiResponse.ok(BaseCode.SMS_SEND_SUCCESS);
    }

    @PostMapping("/sms/confirm")
    public ApiResponse<Void> confirm(@RequestBody @Validated SmsConfirmRequest request) {
        smsService.confirm(request.getPhoneNumber(), request.getVerificationCode());
        return ApiResponse.ok(BaseCode.SMS_VERIFICATION_SUCCESS);
    }
}
