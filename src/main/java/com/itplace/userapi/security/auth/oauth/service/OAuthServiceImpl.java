package com.itplace.userapi.security.auth.oauth.service;

import com.itplace.userapi.benefit.entity.enums.Grade;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.auth.local.dto.response.LoginResponse;
import com.itplace.userapi.security.auth.oauth.dto.request.OAuthSignUpRequest;
import com.itplace.userapi.security.auth.oauth.dto.response.KakaoLoginResult;
import com.itplace.userapi.security.auth.oauth.dto.response.OAuthResult;
import com.itplace.userapi.security.exception.DuplicatePhoneNumberException;
import com.itplace.userapi.security.exception.InvalidCredentialsException;
import com.itplace.userapi.security.exception.UserNotFoundException;
import com.itplace.userapi.security.jwt.JWTConstants;
import com.itplace.userapi.security.jwt.JWTUtil;
import com.itplace.userapi.user.entity.Membership;
import com.itplace.userapi.user.entity.Role;
import com.itplace.userapi.user.entity.SocialAccount;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.MembershipRepository;
import com.itplace.userapi.user.repository.SocialAccountRepository;
import com.itplace.userapi.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Override
    @Transactional(readOnly = true)
    public KakaoLoginResult processKakaoLogin(String code) {
        String kakaoAccessToken = getKakaoAccessToken(code);
        Map<String, Object> userInfo = getKakaoUserInfo(kakaoAccessToken);
//        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account"); 이메일 정보 미사용으로 주석 처리

        String provider = "kakao";
        String providerId = userInfo.get("id").toString();

        log.info("======= provider: {}, providerId: {} =======", provider, providerId);

        Optional<SocialAccount> socialAccountOpt = socialAccountRepository.findByProviderAndProviderId(provider, providerId);

        if (socialAccountOpt.isPresent()) {
            // Case 1: 이미 연동된 기존 사용자 -> 즉시 로그인
            User user = socialAccountOpt.get().getUser();
            OAuthResult authResult = createAuthResultForUser(user);
            return KakaoLoginResult.builder()
                    .isExistingUser(true)
                    .authResult(authResult)
                    .build();
        } else {
            // Case 2: 신규 사용자 -> 가입 절차를 위한 임시 토큰 생성
            String tempToken = jwtUtil.createTempJwt(provider, providerId);
            return KakaoLoginResult.builder()
                    .isExistingUser(false)
                    .tempToken(tempToken)
                    .build();
        }
    }

    @Override
    @Transactional
    public OAuthResult signUpWithOAuth(String tempToken, OAuthSignUpRequest request) {
        Claims claims = getVerifiedClaims(tempToken);
        String provider = claims.get("provider", String.class);
        String providerId = claims.get("providerId", String.class);

        // 신규 가입이므로, 해당 휴대폰 번호로 가입된 유저가 없어야 함
        userRepository.findByPhoneNumber(request.getPhoneNumber()).ifPresent(u -> {
            throw new DuplicatePhoneNumberException(SecurityCode.DUPLICATE_PHONE_NUMBER);
        });

        String membershipId = request.getMembershipId();
        if (membershipId != null && membershipId.isEmpty()) {
            membershipId = null;
        }

        User user = User.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .birthday(request.getBirthday())
                .membershipId(membershipId)
                .password(UUID.randomUUID().toString())
                .role(Role.USER)
                .build();

        user.getSocialAccounts().add(SocialAccount.builder()
                .provider(provider).providerId(providerId).user(user).build());

        userRepository.save(user);

        return createAuthResultForUser(user);
    }

    @Override
    @Transactional
    public OAuthResult linkOAuthAccount(String tempToken, String phoneNumber) {
        Claims claims = getVerifiedClaims(tempToken);
        String provider = claims.get("provider", String.class);
        String providerId = claims.get("providerId", String.class);

        // 계정 연동이므로, 해당 휴대폰 번호로 가입된 유저가 반드시 있어야 함
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException(SecurityCode.USER_NOT_FOUND));

        boolean alreadyLinked = user.getSocialAccounts().stream()
                .anyMatch(sa -> sa.getProvider().equals(provider) && sa.getProviderId().equals(providerId));

        if (!alreadyLinked) {
            user.getSocialAccounts().add(SocialAccount.builder()
                    .provider(provider).providerId(providerId).user(user).build());
        }

        return createAuthResultForUser(user);
    }

    private Claims getVerifiedClaims(String tempToken) {
        if (jwtUtil.isExpired(tempToken) || !"temp".equals(jwtUtil.getCategory(tempToken))) {
            throw new InvalidCredentialsException(SecurityCode.INVALID_TOKEN);
        }
        return jwtUtil.getClaims(tempToken);
    }

    private OAuthResult createAuthResultForUser(User user) {
        String role = user.getRole().getKey();
        String accessToken = jwtUtil.createJwt(user.getId(), role, JWTConstants.CATEGORY_ACCESS);
        String refreshToken = jwtUtil.createJwt(user.getId(), role, JWTConstants.CATEGORY_REFRESH);
        redisTemplate.opsForValue().set("RT:" + user.getId(), refreshToken, jwtUtil.getRefreshTokenValidityInMS(), TimeUnit.MILLISECONDS);

        LoginResponse loginResponse = LoginResponse.builder()
                .name(user.getName())
                .membershipGrade(getMembershipGrade(user.getMembershipId()))
                .build();

        return OAuthResult.builder()
                .loginResponse(loginResponse)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // --- 카카오 통신 Helper 메소드 ---
    private String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", "http://localhost:5173/oauth/callback/kakao");
        params.add("client_secret", kakaoClientSecret);
        params.add("code", code);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        Map<String, Object> response = restTemplate.postForObject(kakaoTokenUri, request, Map.class);
        return (String) response.get("access_token");
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(kakaoUserInfoUri, HttpMethod.GET, request, Map.class).getBody();
    }

    private Grade getMembershipGrade(String membershipId) {
        if (membershipId == null) {
            return null;
        }
        return membershipRepository.findByMembershipId(membershipId)
                .map(Membership::getGrade)
                .orElse(null);
    }
}