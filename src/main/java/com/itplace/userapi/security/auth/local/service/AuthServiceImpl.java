package com.itplace.userapi.security.auth.local.service;

import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.auth.local.dto.request.SignUpRequest;
import com.itplace.userapi.security.auth.local.dto.request.UplusDataRequest;
import com.itplace.userapi.security.auth.local.dto.response.UplusDataResponse;
import com.itplace.userapi.security.exception.DuplicateEmailException;
import com.itplace.userapi.security.exception.DuplicatePhoneNumberException;
import com.itplace.userapi.security.exception.PasswordMismatchException;
import com.itplace.userapi.user.entity.Role;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.UplusDataRepository;
import com.itplace.userapi.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UplusDataRepository uplusDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void logout(Long userId) {
        redisTemplate.delete("RT:" + userId);
    }

    @Override
    public Optional<UplusDataResponse> uplusData(UplusDataRequest request) {
        return uplusDataRepository.findByPhoneNumber(request.getPhoneNumber()).map(UplusDataResponse::from);
    }

    @Override
    @Transactional
    public void signUp(SignUpRequest request) {
        log.info("회원가입 시작 request: {}", request);

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            log.info("비밀번호가 일치하지 않음");
            throw new PasswordMismatchException(SecurityCode.PASSWORD_MISMATCH);
        }

        String membershipId = request.getMembershipId();
        if (membershipId != null && membershipId.isEmpty()) {
            membershipId = null;
        }

        User user = User.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .membershipId(membershipId)
                .birthday(request.getBirthday())
                .role(Role.USER)
                .build();

        log.info("User 정보: {}", user);

        userRepository.findByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber())
                .ifPresent(existUser -> {
                    if (existUser.getEmail().equals(request.getEmail())) {
                        log.info("이메일 중복");
                        throw new DuplicateEmailException(SecurityCode.DUPLICATE_EMAIL);
                    } else if (existUser.getPhoneNumber().equals(request.getPhoneNumber())) {
                        log.info("전화번호 중복");
                        throw new DuplicatePhoneNumberException(SecurityCode.DUPLICATE_PHONE_NUMBER);
                    }
                });
        userRepository.save(user);

        log.info("USER 저장됨");
    }

}
