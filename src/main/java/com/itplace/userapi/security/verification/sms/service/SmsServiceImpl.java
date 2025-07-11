package com.itplace.userapi.security.verification.sms.service;

import com.itplace.userapi.common.BaseCode;
import com.itplace.userapi.common.exception.DuplicatePhoneNumberException;
import com.itplace.userapi.common.exception.SmsVerificationException;
import com.itplace.userapi.user.repository.UserRepository;
import java.time.Duration;
import java.util.Random;
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

    @Value("${twilio.from-phone}")
    private String fromPhone;

    private static final long KEY_TTL_SECONDS = 180;
    private static final long VERIFIED_TTL_SECONDS = 1800;

    @Override
    public void send(String phoneNumber) {
        String code = String.format("%06d", new Random().nextInt(900_000) + 100_000);
        String key = "verify:" + phoneNumber;

        log.info("sms code: {}", code);

        // Redis에 저장 (TTL 5분)
        redisTemplate.opsForValue().set(key, code, Duration.ofSeconds(KEY_TTL_SECONDS));

        // 비용 문제로 실제 문자 보내는 로직은 현재는 주석처리
//        Message.creator(
//                new PhoneNumber(phoneNumber),
//                new PhoneNumber(fromPhone),
//                "[itPlace] 인증번호: " + code + " (3분 이내 유효)"
//        ).create();
    }

    @Override
    public void confirm(String phoneNumber, String verificationCode) {
        String key = "verify:" + phoneNumber;
        String stored = redisTemplate.opsForValue().get(key);

        if (stored == null) {
            // 만료되었거나 없음
            throw new SmsVerificationException(BaseCode.SMS_CODE_EXPIRED);
        }
        if (stored.equals(verificationCode)) {
            // 일치하면 삭제하고 true 반환
            redisTemplate.delete(key);

            if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
                throw new DuplicatePhoneNumberException("이미 가입된 전화번호입니다.");
            }

            String verifiedKey = "verified:" + phoneNumber;
            redisTemplate.opsForValue().set(verifiedKey, "true", Duration.ofSeconds(VERIFIED_TTL_SECONDS));
            System.out.println("인증성공");
            log.info("인증성공");
        } else {
            // 불일치
            throw new SmsVerificationException(BaseCode.SMS_CODE_MISMATCH);
        }
    }
}
