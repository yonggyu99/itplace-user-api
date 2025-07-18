package com.itplace.userapi.security.auth.local.service;

import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.auth.local.dto.CustomUserDetails;
import com.itplace.userapi.security.auth.local.dto.request.LinkLocalToOAuthRequest;
import com.itplace.userapi.security.auth.local.dto.request.LinkOAuthToLocalRequest;
import com.itplace.userapi.security.auth.local.dto.request.LoginRequest;
import com.itplace.userapi.security.auth.local.dto.request.SignUpRequest;
import com.itplace.userapi.security.auth.local.dto.request.UplusDataRequest;
import com.itplace.userapi.security.auth.local.dto.response.LoginWithTokenResponse;
import com.itplace.userapi.security.auth.local.dto.response.TokenResponse;
import com.itplace.userapi.security.auth.local.dto.response.UplusDataResponse;
import com.itplace.userapi.security.auth.oauth.dto.CustomOAuth2User;
import com.itplace.userapi.security.auth.oauth.dto.OAuth2InfoResponse;
import com.itplace.userapi.security.auth.oauth.dto.OAuth2LoginResult;
import com.itplace.userapi.security.exception.DuplicateEmailException;
import com.itplace.userapi.security.exception.DuplicatePhoneNumberException;
import com.itplace.userapi.security.exception.EmailVerificationException;
import com.itplace.userapi.security.exception.InvalidCredentialsException;
import com.itplace.userapi.security.exception.PasswordMismatchException;
import com.itplace.userapi.security.exception.UserNotFoundException;
import com.itplace.userapi.security.jwt.JWTConstants;
import com.itplace.userapi.security.jwt.JWTUtil;
import com.itplace.userapi.user.entity.LinkedAccount;
import com.itplace.userapi.user.entity.Role;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.LinkedAccountRepository;
import com.itplace.userapi.user.repository.UplusDataRepository;
import com.itplace.userapi.user.repository.UserRepository;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final LinkedAccountRepository linkedAccountRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthenticationManager authenticationManager;
    private final UplusDataRepository uplusDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;


    @Override
    public LoginWithTokenResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            String name = user.getName();
            // 변경 필요
            String membershipId = user.getMembershipId();
            Long userId = user.getId();
            Role role = user.getRole();

            String accessToken = jwtUtil.createToken(userId, role, JWTConstants.CATEGORY_ACCESS, user.getEmail());
            String refreshToken = jwtUtil.createToken(userId, role, JWTConstants.CATEGORY_REFRESH, user.getEmail());

            redisTemplate.opsForValue().set("RT:" + userId, refreshToken, 7, TimeUnit.DAYS);

            return LoginWithTokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .name(name)
                    .membershipGrade(null)
                    .build();
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException(SecurityCode.LOGIN_FAIL);
        }
    }

    @Override
    public void logout(Long userId) {
        redisTemplate.delete("RT:" + userId);
    }

    @Override
    public Optional<UplusDataResponse> uplusData(UplusDataRequest request) {
        String registrationId = request.getRegistrationId();
        String storedPhoneNumber = (String) redisTemplate.opsForHash().get(registrationId, "phoneNumber");
        String status = (String) redisTemplate.opsForHash().get(registrationId, "status");

        if (!"SMS_VERIFIED".equals(status)) {
            throw new EmailVerificationException(SecurityCode.SMS_VERIFICATION_NOT_COMPLETED);
        }

        return uplusDataRepository.findByPhoneNumber(storedPhoneNumber).map(UplusDataResponse::from);
    }

    @Override
    @Transactional
    public void signUp(SignUpRequest request) {
        log.info("회원가입 시작 request: {}", request);
        String registrationId = request.getRegistrationId();
        String phoneNumber = request.getPhoneNumber();
        String email = request.getEmail();
        String status = (String) redisTemplate.opsForHash().get(registrationId, "status");

        log.info("status: {}", status);

        if (status == null || !status.equals("EMAIL_VERIFIED")) {
            log.info("이메일 인증 안됨");
            throw new EmailVerificationException(SecurityCode.EMAIL_VERIFICATION_NOT_COMPLETED);
        }

        userRepository.findByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber())
                .ifPresent(user -> {
                    if (user.getEmail().equals(request.getEmail())) {
                        log.info("이메일 중복");
                        throw new DuplicateEmailException(SecurityCode.DUPLICATE_EMAIL);
                    } else if (user.getPhoneNumber().equals(request.getPhoneNumber())) {
                        log.info("전화번호 중복");
                        throw new DuplicatePhoneNumberException(SecurityCode.DUPLICATE_PHONE_NUMBER);
                    }
                });

        String storedPhoneNumber = (String) redisTemplate.opsForHash().get(registrationId, "phoneNumber");
        String storedEmail = (String) redisTemplate.opsForHash().get(registrationId, "email");
        String storedName = (String) redisTemplate.opsForHash().get(registrationId, "name");

        if (!phoneNumber.equals(storedPhoneNumber) || !email.equals(storedEmail)) {
            log.info("폰번호나 이메일이 레디스에 저장된 값이랑 다름");
            throw new EmailVerificationException(SecurityCode.MISMATCHED_VERIFIED_DATA);
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            log.info("비밀번호가 일치하지 않음");
            throw new PasswordMismatchException(SecurityCode.PASSWORD_MISMATCH);
        }

        String membershipId = request.getMembershipId();
        if (membershipId != null && membershipId.isEmpty()) {
            membershipId = null;
        }

        User user = User.builder()
                .email(storedEmail)
                .phoneNumber(storedPhoneNumber)
                .name(storedName)
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .membershipId(membershipId)
                .birthday(request.getBirthday())
                .role(Role.USER)
                .build();

        log.info("User 정보: {}", user);

        redisTemplate.delete(registrationId);

        userRepository.save(user);

        log.info("USER 저장됨");
    }

    @Override
    public void linkLocalToOAuth(LinkLocalToOAuthRequest request) {
        String registrationId = request.getRegistrationId();
        String storedPhoneNumber = (String) redisTemplate.opsForHash().get(registrationId, "phoneNumber");
        String storedStatus = (String) redisTemplate.opsForHash().get(registrationId, "status");
        if (!"EMAIL_VERIFIED".equals(storedStatus)) {
            throw new EmailVerificationException(SecurityCode.EMAIL_VERIFICATION_NOT_COMPLETED);
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new PasswordMismatchException(SecurityCode.PASSWORD_MISMATCH);
        }

        User user = userRepository.findByPhoneNumber(storedPhoneNumber).orElseThrow(
                () -> new UserNotFoundException(SecurityCode.USER_NOT_FOUND));

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        userRepository.save(user);

        redisTemplate.delete(registrationId);

        log.info("사용자(ID:{})에게 로컬 계정 연동이 완료되었습니다.", user.getId());
    }

    @Override
    @Transactional
    public void linkOAuthToLocal(LinkOAuthToLocalRequest request) {
        String registrationId = request.getRegistrationId();
        String provider = (String) redisTemplate.opsForHash().get(registrationId, "provider");
        String providerId = (String) redisTemplate.opsForHash().get(registrationId, "providerId");
        String storedPhoneNumber = (String) redisTemplate.opsForHash().get(registrationId, "phoneNumber");
        String status = (String) redisTemplate.opsForHash().get(registrationId, "status");

        if (!"SMS_VERIFIED".equals(status)) {
            throw new EmailVerificationException(SecurityCode.SMS_VERIFICATION_NOT_COMPLETED);
        }

        User user = userRepository.findByPhoneNumber(storedPhoneNumber).orElseThrow(
                () -> new UserNotFoundException(SecurityCode.USER_NOT_FOUND));

        LinkedAccount linkedAccount = LinkedAccount.builder()
                .user(user)
                .provider(provider)
                .providerId(providerId)
                .build();
        linkedAccountRepository.save(linkedAccount);

        redisTemplate.delete(registrationId);

        log.info("사용자(ID:{})에게 SMS 인증 기반 로컬 계정 연동이 완료되었습니다.", user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public OAuth2LoginResult oAuth2Login(CustomOAuth2User oAuth2User) {
        // 1. 기존에 연동된 계정인지 확인
        return linkedAccountRepository.findByProviderAndProviderId(oAuth2User.getProvider(), oAuth2User.getProviderId())
                .map(linkedAccount -> {
                    // 1-1. 연동 계정이 있다면: 로그인 성공 처리 및 토큰 발급
                    User user = linkedAccount.getUser();
                    log.info("기존 연동 계정을 확인했습니다. User ID: {}. 토큰을 발급합니다.", user.getId());
                    return OAuth2LoginResult.success(createTokenResponse(user));
                })
                .orElseGet(() -> {
                    // 1-2. 연동 계정이 없다면: 회원가입 필요 응답
                    OAuth2InfoResponse oAuth2InfoResponse = oAuth2User.getOAuth2InfoResponse();
                    log.info("미연동 소셜 계정입니다. 회원가입 절차를 안내합니다. Registration ID: {}", oAuth2InfoResponse.getRegistrationId());
                    return OAuth2LoginResult.signupRequired(oAuth2InfoResponse);
                });
    }

    private TokenResponse createTokenResponse(User user) {
        String accessToken = jwtUtil.createToken(user.getId(), user.getRole(), JWTConstants.CATEGORY_ACCESS, user.getEmail());
        String refreshToken = jwtUtil.createToken(user.getId(), user.getRole(), JWTConstants.CATEGORY_REFRESH, user.getEmail());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
