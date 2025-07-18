package com.itplace.userapi.security.auth.local.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.auth.local.dto.CustomUserDetails;
import com.itplace.userapi.security.auth.local.dto.request.LoginRequest;
import com.itplace.userapi.security.auth.local.dto.response.LoginResponse;
import com.itplace.userapi.security.auth.local.dto.response.LoginWithTokenResponse;
import com.itplace.userapi.security.auth.local.service.AuthService;
import com.itplace.userapi.security.jwt.JWTConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증, 인가, 로그인, 회원가입 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Validated LoginRequest request, HttpServletResponse response) {
        LoginWithTokenResponse loginWithTokenResponse = authService.login(request);

        response.addCookie(createCookie(JWTConstants.CATEGORY_ACCESS, loginWithTokenResponse.getAccessToken()));
        response.addCookie(createCookie(JWTConstants.CATEGORY_REFRESH, loginWithTokenResponse.getRefreshToken()));

        LoginResponse loginResponse = LoginResponse.builder()
                .name(loginWithTokenResponse.getName())
                .membershipGrade(loginWithTokenResponse.getMembershipGrade())
                .build();

        ApiResponse<LoginResponse> body = ApiResponse.of(SecurityCode.LOGIN_SUCCESS, loginResponse);
        return new ResponseEntity<>(body, body.getStatus());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        authService.logout(userDetails.getUser().getId());
        response.addCookie(createExpiredCookie(JWTConstants.CATEGORY_ACCESS));
        response.addCookie(createExpiredCookie(JWTConstants.CATEGORY_REFRESH));
        ApiResponse<Void> body = ApiResponse.ok(SecurityCode.LOGOUT_SUCCESS);
        return new ResponseEntity<>(body, body.getStatus());
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24 * 7);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    private Cookie createExpiredCookie(String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 즉시 삭제
        cookie.setPath("/");
        return cookie;
    }
}
