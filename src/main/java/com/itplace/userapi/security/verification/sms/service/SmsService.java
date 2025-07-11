package com.itplace.userapi.security.verification.sms.service;

public interface SmsService {

    void send(String phoneNumber);

    void confirm(String phoneNumber, String verificationCode);
}
