package com.itplace.userapi.security.auth.local.service;

import com.itplace.userapi.security.auth.local.dto.request.LinkLocalToOAuthRequest;
import com.itplace.userapi.security.auth.local.dto.request.LoginRequest;
import com.itplace.userapi.security.auth.local.dto.request.SignUpRequest;
import com.itplace.userapi.security.auth.local.dto.request.UplusDataRequest;
import com.itplace.userapi.security.auth.local.dto.response.TokenResponse;
import com.itplace.userapi.security.auth.local.dto.response.UplusDataResponse;
import com.itplace.userapi.security.auth.oauth.dto.CustomOAuth2User;
import com.itplace.userapi.security.auth.oauth.dto.OAuth2LoginResult;
import java.util.Optional;

public interface AuthService {

    TokenResponse login(LoginRequest request);

    void logout(Long userId);

    void signUp(SignUpRequest request);

    OAuth2LoginResult oAuth2Login(CustomOAuth2User customOAuth2User);

    Optional<UplusDataResponse> uplusData(UplusDataRequest request);

    void linkLocalToOAuth(LinkLocalToOAuthRequest request);

    void linkOAuthToLocal(String registrationId);
}
