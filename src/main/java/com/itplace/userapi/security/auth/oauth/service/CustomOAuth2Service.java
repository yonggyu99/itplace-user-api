package com.itplace.userapi.security.auth.oauth.service;

import com.itplace.userapi.security.auth.oauth.dto.CustomOAuth2User;
import com.itplace.userapi.security.auth.oauth.dto.KakaoResponse;
import com.itplace.userapi.security.auth.oauth.dto.OAuth2InfoResponse;
import com.itplace.userapi.security.auth.oauth.dto.OAuth2Response;
import com.itplace.userapi.user.entity.LinkedAccount;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.LinkedAccountRepository;
import com.itplace.userapi.user.repository.UserRepository;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final LinkedAccountRepository linkedAccountRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // provider 랑 동일한 결과
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // provider, providerId, email 3가지를 가지고 있음
        OAuth2Response oAuth2Response = getOAuth2Response(registrationId, oAuth2User);

        // 1. provider 와 providerId로 이미 연동이 완료된 계정이 있는지 확인 (정회원 재로그인)
        Optional<LinkedAccount> linkedAccountOpt = linkedAccountRepository
                .findByProviderAndProviderId(oAuth2Response.getProvider(), oAuth2Response.getProviderId());

        // OAuth 에 연동된 계정이 있다면
        if (linkedAccountOpt.isPresent()) {
            log.info("기존에 연동된 OAuth 계정으로 로그인합니다.");
            User user = linkedAccountOpt.get().getUser();
            OAuth2InfoResponse oAuth2InfoResponse = new OAuth2InfoResponse();
            oAuth2InfoResponse.setProviderId(user.getId().toString());
            oAuth2InfoResponse.setEmail(user.getEmail());
            oAuth2InfoResponse.setRole(user.getRole());
            oAuth2InfoResponse.setProvider(oAuth2Response.getProvider()); // provider 정보 추가
            return new CustomOAuth2User(oAuth2InfoResponse, oAuth2User.getAttributes());
        }

        // Case B: 연동된 계정이 없다면, 핸드폰 인증을 위한 임시 정보 생성
        String tempKey = UUID.randomUUID().toString();

        // Redis에 저장할 OAuth 정보 DTO (별도 클래스로 만드는 것을 추천)
        Map<String, String> tempOAuthInfo = new HashMap<>();
        tempOAuthInfo.put("provider", registrationId);
        tempOAuthInfo.put("providerId", oAuth2User.getName());
        tempOAuthInfo.put("email", oAuth2User.getAttribute("email"));

        // Redis에 임시 정보 저장 (10분 유효)
        redisTemplate.opsForValue().set("oauth-pending:" + tempKey, tempOAuthInfo, Duration.ofMinutes(10));

        // 핸드폰 인증 페이지로 보낼 임시 사용자 생성
        // 속성에 임시 키를 넣어 핸들러에서 사용하도록 함
        Map<String, Object> tempAttributes = new HashMap<>(oAuth2User.getAttributes());
        tempAttributes.put("tempKey", tempKey);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_GUEST")),
                tempAttributes, // 임시 키가 포함된 속성
                "email"
        );
    }

    private OAuth2Response getOAuth2Response(String registrationId, OAuth2User oAuth2User) {
        if (registrationId.equals("kakao")) {
            return new KakaoResponse(oAuth2User.getAttributes());
        }
        return null;
    }
}
