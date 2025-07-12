package com.itplace.userapi.security.verification.sms.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.verify-service-sid}")
    private String serviceSid;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    @Bean
    public String verifyServiceSid() {
        return serviceSid;
    }
}