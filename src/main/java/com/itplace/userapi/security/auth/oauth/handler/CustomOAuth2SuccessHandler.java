package com.itplace.userapi.security.auth.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.common.BaseCode;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.auth.local.service.AuthService;
import com.itplace.userapi.security.auth.oauth.dto.CustomOAuth2User;
import com.itplace.userapi.security.auth.oauth.dto.OAuth2InfoResponse;
import com.itplace.userapi.security.auth.oauth.dto.OAuth2LoginResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public CustomOAuth2SuccessHandler(@Lazy AuthService authService, ObjectMapper objectMapper) {
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        OAuth2LoginResult oAuth2LoginResult = authService.oAuth2Login(oAuth2User);
        BaseCode code = oAuth2LoginResult.getCode();
        OAuth2InfoResponse oAuth2InfoResponse = oAuth2LoginResult.getOAuth2InfoResponse();

        if (code == SecurityCode.LOGIN_SUCCESS) {
            response.sendRedirect("/oauth_registration_test.html?status=LOGIN_SUCCESS");
        } else if (code == SecurityCode.SIGNUP_REQUIRED) {
            String registrationId = oAuth2InfoResponse.getRegistrationId();
            response.sendRedirect("/oauth_registration_test.html?status=SIGNUP_REQUIRED&registrationId=" + registrationId);
        } else {
            response.sendRedirect("/login?error");
        }
    }

    private void writeJsonResponse(HttpServletResponse response, ApiResponse<?> apiResponse) throws IOException {
        response.setStatus(apiResponse.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
