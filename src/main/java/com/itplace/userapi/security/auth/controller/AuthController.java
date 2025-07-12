package com.itplace.userapi.security.auth.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.common.BaseCode;
import com.itplace.userapi.security.auth.dto.CustomUserDetails;
import com.itplace.userapi.security.auth.dto.request.LoginRequest;
import com.itplace.userapi.security.auth.dto.response.TokenResponse;
import com.itplace.userapi.security.auth.service.AuthService;
import com.itplace.userapi.security.verification.jwt.JWTConstants;
import com.itplace.userapi.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse tokens = authService.login(request);
        response.addCookie(createCookie(JWTConstants.CATEGORY_ACCESS, tokens.getAccessToken()));
        response.addCookie(createCookie(JWTConstants.CATEGORY_REFRESH, tokens.getRefreshToken()));
        return ApiResponse.ok(BaseCode.LOGIN_SUCCESS);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        authService.logout(userDetails.getUser().getId());

        response.addCookie(createExpiredCookie("access_token"));
        response.addCookie(createExpiredCookie("refresh_token"));

        return ApiResponse.ok(BaseCode.LOGOUT_SUCCESS);
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
