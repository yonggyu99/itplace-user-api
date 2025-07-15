package com.itplace.userapi.security.verification.sms.service;

import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.exception.SmsVerificationException;
import com.itplace.userapi.security.verification.sms.dto.SmsConfirmRequest;
import com.itplace.userapi.security.verification.sms.dto.SmsConfirmResponse;
import com.itplace.userapi.security.verification.sms.dto.SmsVerificationRequest;
import com.itplace.userapi.security.verification.sms.dto.SmsVerificationResponse;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.entity.UserStatus;
import com.itplace.userapi.user.repository.UplusDataRepository;
import com.itplace.userapi.user.repository.UserRepository;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final UplusDataRepository uplusDataRepository;

    @Value("${twilio.from-phone}")
    private String fromPhone;

    private static final long KEY_TTL_SECONDS = 180; // 3분
    private static final long VERIFIED_TTL_SECONDS = 1800; // 30분

    @Override
    public SmsVerificationResponse send(SmsVerificationRequest request) {
        log.info("SmsVerificationRequest: {}", request);
        String registrationId = request.getRegistrationId();
        if (registrationId == null || registrationId.isEmpty()) {
            registrationId = UUID.randomUUID().toString();
        }
        String name = request.getName();
        String phoneNumber = request.getPhoneNumber();

        redisTemplate.opsForHash().put(registrationId, "name", name);
        redisTemplate.opsForHash().put(registrationId, "phoneNumber", phoneNumber);
        redisTemplate.opsForHash().put(registrationId, "status", "SMS_SENT");

        String code = String.format("%06d", new Random().nextInt(900_000) + 100_000);
        String key = "verify:" + registrationId + ":" + phoneNumber;

        log.info("sms code: {}", code);

        redisTemplate.opsForValue().set(key, code, Duration.ofSeconds(KEY_TTL_SECONDS));

        // 비용 문제로 실제 문자 보내는 로직은 현재는 주석처리
//        Message.creator(
//                new PhoneNumber(phoneNumber),
//                new PhoneNumber(fromPhone),
//                "[itPlace] 인증번호: " + code + " (3분 이내 유효)"
//        ).create();

        return SmsVerificationResponse.builder()
                .registrationId(registrationId)
                .build();
    }

    @Override
    public SmsConfirmResponse confirm(SmsConfirmRequest request) {
        log.info("SmsConfirmRequest: {}", request);
        String registrationId = request.getRegistrationId();
        String phoneNumber = request.getPhoneNumber();
        String code = request.getVerificationCode();

        String key = "verify:" + registrationId + ":" + phoneNumber;
        String stored = redisTemplate.opsForValue().get(key);

        if (stored == null) {
            // 만료되었거나 없음
            throw new SmsVerificationException(SecurityCode.SMS_CODE_EXPIRED);
        }

        if (!stored.equals(code)) {
            throw new SmsVerificationException(SecurityCode.SMS_CODE_MISMATCH);
        }

        // 일치하면 삭제하고 true 반환
        redisTemplate.delete(key);

        String storedPhoneNumber = (String) redisTemplate.opsForHash().get(registrationId, "phoneNumber");
        String storedStatus = (String) redisTemplate.opsForHash().get(registrationId, "status");

        // registrationId 가 유효하지 않거나, phoneNumber 가 불일치하거나, 상태가 이상할 경우
        if (storedPhoneNumber == null || !storedPhoneNumber.equals(phoneNumber) || !"SMS_SENT".equals(storedStatus)) {
            throw new SmsVerificationException(SecurityCode.INVALID_REGISTRATION_SESSION);
        }
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        UserStatus userStatus = userOpt.isPresent() ? UserStatus.EXISTING_USER : UserStatus.NEW_USER;
        boolean isLocalUser = userOpt.map(user -> user.getPassword() != null).orElse(false);
        boolean uplusData = uplusDataRepository.findByPhoneNumber(phoneNumber).isPresent();

        redisTemplate.opsForHash().put(registrationId, "status", "SMS_VERIFIED");
        redisTemplate.expire(registrationId, Duration.ofSeconds(VERIFIED_TTL_SECONDS)); // Set TTL for the registration session

        log.info("SMS 인증 성공. UserStatus: {}, isLocalUser: {}, UplusData: {}", userStatus, isLocalUser, uplusData);

        return SmsConfirmResponse.builder()
                .registrationId(registrationId)
                .userStatus(userStatus)
                .isLocalUser(isLocalUser)
                .uplusDataExists(uplusData)
                .build();
    }
}
