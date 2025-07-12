package com.itplace.userapi.security.verification.sms.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SmsConfirmResponse {
    private String registrationId;
    private boolean uplusDataExists;
}