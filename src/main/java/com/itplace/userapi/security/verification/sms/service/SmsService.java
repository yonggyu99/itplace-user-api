package com.itplace.userapi.security.verification.sms.service;

import com.itplace.userapi.security.verification.sms.dto.SmsConfirmRequest;
import com.itplace.userapi.security.verification.sms.dto.SmsConfirmResponse;
import com.itplace.userapi.security.verification.sms.dto.SmsVerificationRequest;
import com.itplace.userapi.security.verification.sms.dto.SmsVerificationResponse;

public interface SmsService {

    SmsVerificationResponse send(SmsVerificationRequest request);

    SmsConfirmResponse confirm(SmsConfirmRequest request);
}
