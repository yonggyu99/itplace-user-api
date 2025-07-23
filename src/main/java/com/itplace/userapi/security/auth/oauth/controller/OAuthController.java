package com.itplace.userapi.security.auth.oauth.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.auth.local.dto.response.LoginResponse;
import com.itplace.userapi.security.auth.oauth.dto.request.KakaoCodeRequest;
import com.itplace.userapi.security.auth.oauth.dto.request.OAuthSignUpRequest;
import com.itplace.userapi.security.auth.oauth.dto.response.KakaoLoginResult;
import com.itplace.userapi.security.auth.oauth.dto.response.OAuthResult;
import com.itplace.userapi.security.auth.oauth.service.OAuthService;
import com.itplace.userapi.security.jwt.JWTConstants;
import com.itplace.userapi.security.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/oauth")
public class OAuthController {

    private final OAuthService oAuthService;
    private final JWTUtil jwtUtil;

    /**
     * React에서 인가 코드를 받아 카카오 로그인을 처리하는 첫 관문입니다.
     * 기존 회원이면 즉시 로그인, 신규 회원이면 임시 토큰을 발급합니다.
     */
    @PostMapping("/kakao")
    public ResponseEntity<ApiResponse<LoginResponse>> kakaoLogin(@RequestBody KakaoCodeRequest request, HttpServletResponse httpServletResponse) {
        KakaoLoginResult result = oAuthService.processKakaoLogin(request.getCode());

        if (result.isExistingUser()) {
            // Case 1: 기존 사용자 -> 즉시 로그인 성공
            OAuthResult oAuthResult = result.getAuthResult();
            setTokensToCookie(httpServletResponse, oAuthResult.getAccessToken(), oAuthResult.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.of(SecurityCode.LOGIN_SUCCESS, oAuthResult.getLoginResponse()));
        } else {
            // Case 2: 신규 사용자 -> 임시 토큰 발급 및 휴대폰 인증 필요
            ResponseCookie tempTokenCookie = ResponseCookie.from("tempToken", result.getTempToken())
                    .path("/")
                    .secure(true)
                    .sameSite("None")
                    .httpOnly(true)
                    .maxAge(TimeUnit.MINUTES.toSeconds(10))
                    .build();
            httpServletResponse.addHeader("Set-Cookie", tempTokenCookie.toString());
            // 본문(data) 없이, 휴대폰 인증이 필요하다는 상태를 응답
            return ResponseEntity.ok(ApiResponse.of(SecurityCode.PRE_AUTHENTICATION_SUCCESS, null));
        }
    }

    /**
     * 신규 사용자가 휴대폰 인증 후, 추가 정보를 입력하여 최종 가입할 때 호출됩니다.
     */
    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<LoginResponse>> oauthSignUpNew(
            @CookieValue("tempToken") String tempToken,
            @RequestBody @Validated OAuthSignUpRequest request,
            HttpServletResponse httpServletResponse
    ) {
        OAuthResult result = oAuthService.signUpWithOAuth(tempToken, request);
        setTokensToCookie(httpServletResponse, result.getAccessToken(), result.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.of(SecurityCode.LOGIN_SUCCESS, result.getLoginResponse()));
    }

    /**
     * 기존 사용자가 휴대폰 인증 후, 자신의 계정에 소셜 계정을 연동할 때 호출됩니다.
     */
    @PostMapping("/link")
    public ResponseEntity<ApiResponse<LoginResponse>> oauthSignUpLink(
            @CookieValue("tempToken") String tempToken,
            @RequestBody Map<String, String> requestBody,
            HttpServletResponse httpServletResponse
    ) {
        String phoneNumber = requestBody.get("phoneNumber");
        OAuthResult result = oAuthService.linkOAuthAccount(tempToken, phoneNumber);
        setTokensToCookie(httpServletResponse, result.getAccessToken(), result.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.of(SecurityCode.LOGIN_SUCCESS, result.getLoginResponse()));
    }

    /**
     * Access Token과 Refresh Token을 HttpOnly 쿠키에 설정하는 유틸리티 메소드입니다.
     */
    private void setTokensToCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessTokenCookie = ResponseCookie.from(JWTConstants.CATEGORY_ACCESS, accessToken)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .maxAge(jwtUtil.getAccessTokenValidityInMS() / 1000)
                .build();
        response.addHeader("Set-Cookie", accessTokenCookie.toString());

        ResponseCookie refreshTokenCookie = ResponseCookie.from(JWTConstants.CATEGORY_REFRESH, refreshToken)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .maxAge(jwtUtil.getRefreshTokenValidityInMS() / 1000)
                .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}