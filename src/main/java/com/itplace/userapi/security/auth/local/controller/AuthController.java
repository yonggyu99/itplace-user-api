package com.itplace.userapi.security.auth.local.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.auth.local.dto.CustomUserDetails;
import com.itplace.userapi.security.auth.local.service.AuthService;
import com.itplace.userapi.security.jwt.JWTConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증, 인가, 로그인, 회원가입 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response) {
        authService.logout(userDetails.getUser().getId());
        response.addCookie(createExpiredCookie(JWTConstants.CATEGORY_ACCESS));
        response.addCookie(createExpiredCookie(JWTConstants.CATEGORY_REFRESH));
        ApiResponse<Void> body = ApiResponse.ok(SecurityCode.LOGOUT_SUCCESS);
        return new ResponseEntity<>(body, body.getStatus());
    }

    private Cookie createExpiredCookie(String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 즉시 삭제
        cookie.setPath("/");
        return cookie;
    }
}
