package com.itplace.userapi.security.verification.sms.service;

import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.exception.SmsVerificationException;
import com.itplace.userapi.security.verification.OtpUtil;
import com.itplace.userapi.security.verification.sms.dto.SmsConfirmRequest;
import com.itplace.userapi.security.verification.sms.dto.SmsConfirmResponse;
import com.itplace.userapi.security.verification.sms.dto.SmsVerificationRequest;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.entity.UserStatus;
import com.itplace.userapi.user.repository.UplusDataRepository;
import com.itplace.userapi.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final UplusDataRepository uplusDataRepository;
    private final OtpUtil otpUtil;

    @Value("${twilio.from-phone}")
    private String fromPhone;

    private static final long KEY_TTL_SECONDS = 180; // 3분
    private static final long VERIFIED_TTL_SECONDS = 1800; // 30분

    @Override
    public void send(SmsVerificationRequest request) {
        log.info("SmsVerificationRequest: {}", request);

        String name = request.getName();
        String phoneNumber = request.getPhoneNumber();

        String key = "sms:" + phoneNumber;
        String code = otpUtil.generateSmsOtp(phoneNumber);

        log.info("sms key: {}", key);
        log.info("sms code: {}", code);

        // 비용 문제로 실제 문자 보내는 로직은 현재는 주석처리
//        Message.creator(
//                new PhoneNumber(phoneNumber),
//                new PhoneNumber(fromPhone),
//                "[itPlace] 인증번호: " + code + " (3분 이내 유효)"
//        ).create();
    }

    @Override
    @Transactional(readOnly = true)
    public SmsConfirmResponse confirm(SmsConfirmRequest request) {
        log.info("SmsConfirmRequest: {}", request);
        String phoneNumber = request.getPhoneNumber();
        String code = request.getVerificationCode();

        if (otpUtil.validateSmsOtp(phoneNumber, code)) {
            log.info("SMS 인증 성공");
            Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
            UserStatus userStatus = userOpt.isPresent() ? UserStatus.EXISTING_USER : UserStatus.NEW_USER;
            boolean isLocalUser = userOpt.map(user -> user.getEmail() != null).orElse(false);
            boolean uplusData = uplusDataRepository.findByPhoneNumber(phoneNumber).isPresent();
            log.info("UserStatus: {}, isLocalUser: {}, UplusData: {}", userStatus, isLocalUser, uplusData);
            return SmsConfirmResponse.builder()
                    .userStatus(userStatus)
                    .localUser(isLocalUser)
                    .uplusDataExists(uplusData)
                    .build();
        } else {
            log.info("SMS 인증 실패");
            throw new SmsVerificationException(SecurityCode.SMS_CODE_FAILURE);
        }
    }
}
