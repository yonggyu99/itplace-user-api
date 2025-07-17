package com.itplace.userapi.security.auth.oauth.service;

import com.itplace.userapi.security.auth.oauth.dto.CustomOAuth2User;
import com.itplace.userapi.security.auth.oauth.dto.KakaoResponse;
import com.itplace.userapi.security.auth.oauth.dto.OAuth2InfoResponse;
import com.itplace.userapi.security.auth.oauth.dto.OAuth2Response;
import com.itplace.userapi.user.entity.LinkedAccount;
import com.itplace.userapi.user.entity.Role;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.LinkedAccountRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2Service extends DefaultOAuth2UserService {

    private final LinkedAccountRepository linkedAccountRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth 로그인 시작");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = getOAuth2Response(provider, oAuth2User);

        // 1. provider와 providerId로 이미 연동된 계정이 있는지 확인 (기존 회원 로그인)
        Optional<LinkedAccount> linkedAccountOpt = linkedAccountRepository
                .findByProviderAndProviderId(oAuth2Response.getProvider(), oAuth2Response.getProviderId());

        if (linkedAccountOpt.isPresent()) {
            log.info("기존에 연동된 OAuth 계정으로 로그인합니다. Provider: {}, ProviderId: {}", oAuth2Response.getProvider(), oAuth2Response.getProviderId());
            User user = linkedAccountOpt.get().getUser();

            OAuth2InfoResponse oAuth2InfoResponse = OAuth2InfoResponse.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .role("ROLE_" + user.getRole().name())
                    .provider(provider)
                    .providerId(oAuth2Response.getProviderId())
                    .build();

            return new CustomOAuth2User(oAuth2InfoResponse, oAuth2User.getAttributes());
        }
        // 여기까지는 완벽

        // 2. 연동된 계정이 없다면, 이메일 중복 여부와 관계없이 무조건 본인인증 절차 진행
        log.info("신규 소셜 로그인 또는 미연동 계정입니다. 회원가입을 시작합니다. Provider: {}", provider);
        String registrationId = createGuestSession(provider, oAuth2Response);

        // 신규 회원을 위한 임시 CustomOAuth2User 생성 (GUEST 권한)
        OAuth2InfoResponse oAuth2InfoResponse = OAuth2InfoResponse.builder()
                .registrationId(registrationId)
                .provider(provider)
                .providerId(oAuth2Response.getProviderId())
                .email(oAuth2Response.getEmail())
                .role("ROLE_" + Role.GUEST.name())
                .build();

        return new CustomOAuth2User(oAuth2InfoResponse, oAuth2User.getAttributes());
    }

    /**
     * 신규 사용자를 위해 소셜 로그인 정보를 Redis에 임시 저장하고, 세션 ID를 반환합니다.
     */
    private String createGuestSession(String provider, OAuth2Response oAuth2Response) {
        String registrationId = UUID.randomUUID().toString();

        redisTemplate.opsForHash().put(registrationId, "provider", provider);
        redisTemplate.opsForHash().put(registrationId, "providerId", oAuth2Response.getProviderId());
        redisTemplate.opsForHash().put(registrationId, "email", oAuth2Response.getEmail());
        redisTemplate.expire(registrationId, 30, TimeUnit.MINUTES);

        log.info("Redis에 임시 소셜 정보 저장 완료. Registration ID: {}", registrationId);
        return registrationId;
    }

    private OAuth2Response getOAuth2Response(String provider, OAuth2User oAuth2User) {
        if (provider.equals("kakao")) {
            return new KakaoResponse(oAuth2User.getAttributes());
        }
        return null;
    }
}
