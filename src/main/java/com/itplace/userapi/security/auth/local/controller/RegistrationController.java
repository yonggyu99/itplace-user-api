package com.itplace.userapi.security.auth.local.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.auth.local.dto.request.SignUpRequest;
import com.itplace.userapi.security.auth.local.dto.request.UplusDataRequest;
import com.itplace.userapi.security.auth.local.dto.response.UplusDataResponse;
import com.itplace.userapi.security.auth.local.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증, 인가, 로그인, 회원가입 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody @Validated SignUpRequest request) {
        authService.signUp(request);
        ApiResponse<Void> body = ApiResponse.ok(SecurityCode.SIGNUP_SUCCESS);
        return ResponseEntity
                .status(body.getStatus())
                .body(body);
    }

    @PostMapping("/loadUplusData")
    public ResponseEntity<ApiResponse<UplusDataResponse>> uplusData(@RequestBody UplusDataRequest request) {
        return authService.uplusData(request)
                .map(data -> {
                    ApiResponse<UplusDataResponse> body = ApiResponse.of(
                            SecurityCode.UPLUS_DATA_FOUND, data);
                    return ResponseEntity
                            .status(body.getStatus())
                            .body(body);
                })
                .orElseGet(() -> {
                    ApiResponse<UplusDataResponse> body = ApiResponse.of(
                            SecurityCode.UPLUS_DATA_NOT_FOUND, null);
                    return ResponseEntity
                            .status(body.getStatus())
                            .body(body);
                });
    }
}
