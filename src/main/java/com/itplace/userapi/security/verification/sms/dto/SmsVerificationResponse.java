package com.itplace.userapi.security.verification.sms.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SmsVerificationResponse {
    private String registrationId;
}
