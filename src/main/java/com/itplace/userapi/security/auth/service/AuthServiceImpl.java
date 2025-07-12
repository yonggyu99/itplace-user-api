package com.itplace.userapi.security.auth.service;

import com.itplace.userapi.common.BaseCode;
import com.itplace.userapi.common.exception.DuplicateEmailException;
import com.itplace.userapi.common.exception.DuplicatePhoneNumberException;
import com.itplace.userapi.common.exception.EmailVerificationException;
import com.itplace.userapi.common.exception.PasswordMismatchException;
import com.itplace.userapi.security.auth.dto.CustomUserDetails;
import com.itplace.userapi.security.auth.dto.request.LoginRequest;
import com.itplace.userapi.security.auth.dto.request.SignUpRequest;
import com.itplace.userapi.security.auth.dto.response.TokenResponse;
import com.itplace.userapi.security.verification.jwt.JWTConstants;
import com.itplace.userapi.security.verification.jwt.JWTUtil;
import com.itplace.userapi.user.entity.Role;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        Long userId = user.getId();
        Role role = user.getRole();

        String accessToken = jwtUtil.createToken(userId, role, JWTConstants.CATEGORY_ACCESS, null, null, user.getEmail());
        String refreshToken = jwtUtil.createToken(userId, role, JWTConstants.CATEGORY_REFRESH, null, null, user.getEmail());

        // Redis에 Refresh Token 저장
        redisTemplate.opsForValue().set("RT:" + userId, refreshToken, 7, TimeUnit.DAYS);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(Long userId) {

    }

    @Override
    public void signUp(SignUpRequest request) {
        String registrationId = request.getRegistrationId();
        String phoneNumber = request.getPhoneNumber();
        String email = request.getEmail();
        String registrationKey = "registration:" + registrationId;
        String status = (String) redisTemplate.opsForHash().get(registrationKey, "status");

        if (status == null || !status.equals("EMAIL_VERIFIED")) {
            throw new EmailVerificationException(BaseCode.EMAIL_VERIFICATION_NOT_COMPLETED);
        }

        userRepository.findByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber())
                .ifPresent(user -> {
                    if (user.getEmail().equals(request.getEmail())) {
                        throw new DuplicateEmailException(BaseCode.DUPLICATE_EMAIL);
                    } else if (user.getPhoneNumber().equals(request.getPhoneNumber())) {
                        throw new DuplicatePhoneNumberException(BaseCode.DUPLICATE_PHONE_NUMBER);
                    }
                });

        String storedPhoneNumber = (String) redisTemplate.opsForHash().get(registrationKey, "phoneNumber");
        String storedEmail = (String) redisTemplate.opsForHash().get(registrationKey, "email");
        String storedName = (String) redisTemplate.opsForHash().get(registrationKey, "name");

        if (!phoneNumber.equals(storedPhoneNumber) || !email.equals(storedEmail)) {
            throw new EmailVerificationException(BaseCode.MISMATCHED_VERIFIED_DATA);
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new PasswordMismatchException(BaseCode.PASSWORD_MISMATCH);
        }

        User user = User.builder()
                .email(storedEmail)
                .phoneNumber(storedPhoneNumber)
                .name(storedName)
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .birthday(request.getBirthday())
                .role(Role.USER)
                .build();

        redisTemplate.delete(registrationKey);

        userRepository.save(user);
    }
}
