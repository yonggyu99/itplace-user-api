package com.itplace.userapi.security.auth.oauth.service;

import com.itplace.userapi.security.auth.oauth.dto.request.OAuthSignUpRequest;
import com.itplace.userapi.security.auth.oauth.dto.response.KakaoLoginResult;
import com.itplace.userapi.security.auth.oauth.dto.response.OAuthResult;

public interface OAuthService {

    KakaoLoginResult processKakaoLogin(String code);

    // 신규 OAuth 사용자 가입
    OAuthResult signUpWithOAuth(String tempToken, OAuthSignUpRequest request);

    // 기존 계정에 OAuth 계정 연동
    OAuthResult linkOAuthAccount(String tempToken, String phoneNumber);
}
