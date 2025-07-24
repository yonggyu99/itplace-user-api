package com.itplace.userapi.user.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.auth.common.PrincipalDetails;
import com.itplace.userapi.security.auth.local.dto.CustomUserDetails;
import com.itplace.userapi.security.exception.UserNotFoundException;
import com.itplace.userapi.security.verification.email.dto.EmailConfirmRequest;
import com.itplace.userapi.security.verification.email.dto.EmailVerificationRequest;
import com.itplace.userapi.security.verification.email.service.EmailService;
import com.itplace.userapi.security.verification.sms.dto.SmsVerificationRequest;
import com.itplace.userapi.security.verification.sms.service.SmsService;
import com.itplace.userapi.user.UserCode;
import com.itplace.userapi.user.dto.request.FindEmailConfirmRequest;
import com.itplace.userapi.user.dto.request.ResetPasswordRequest;
import com.itplace.userapi.user.dto.response.FindEmailResponse;
import com.itplace.userapi.user.dto.response.FindPasswordConfirmResponse;
import com.itplace.userapi.user.dto.response.UserInfoResponse;
import com.itplace.userapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SmsService smsService;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new UserNotFoundException(SecurityCode.USER_NOT_FOUND);
        }
        UserInfoResponse userInfoDto = userService.getUserInfo(userDetails.getUserId());
        ApiResponse<?> body = ApiResponse.of(UserCode.USER_INFO_SUCCESS, userInfoDto);

        return ResponseEntity
                .status(body.getStatus())
                .body(body);
    }

    @PostMapping("/findEmail")
    public ResponseEntity<ApiResponse<Void>> findEmail(@RequestBody @Validated SmsVerificationRequest request) {
        log.info("문자 request:{} ", request);
        smsService.send(request);
        ApiResponse<Void> body = ApiResponse.ok(SecurityCode.SMS_SEND_SUCCESS);
        return ResponseEntity
                .status(body.getStatus())
                .body(body);
    }

    @PostMapping("/findEmail/confirm")
    public ResponseEntity<ApiResponse<FindEmailResponse>> findEmailConfirm(
            @RequestBody @Validated FindEmailConfirmRequest request) {
        log.info("findEmailConfirm request: {}", request);
        FindEmailResponse response = userService.findEmailConfirm(request);
        ApiResponse<FindEmailResponse> body = ApiResponse.of(UserCode.EMAIL_FIND_SUCCESS, response);
        return ResponseEntity
                .status(body.getStatus())
                .body(body);
    }

    @PostMapping("/findPassword")
    public ResponseEntity<ApiResponse<Void>> findPassword(@RequestBody @Validated EmailVerificationRequest request) {
        emailService.send(request);
        ApiResponse<Void> body = ApiResponse.ok(SecurityCode.EMAIL_SEND_SUCCESS);
        return ResponseEntity
                .status(body.getStatus())
                .body(body);
    }

    @PostMapping("/findPassword/confirm")
    public ResponseEntity<ApiResponse<FindPasswordConfirmResponse>> findPasswordConfirm(
            @RequestBody @Validated EmailConfirmRequest request) {
        FindPasswordConfirmResponse response = userService.findPasswordConfirm(request);
        ApiResponse<FindPasswordConfirmResponse> body = ApiResponse.of(SecurityCode.EMAIL_VERIFICATION_SUCCESS,
                response);
        return ResponseEntity
                .status(body.getStatus())
                .body(body);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Validated ResetPasswordRequest request) {
        userService.resetPassword(request);
        ApiResponse<Void> body = ApiResponse.ok(SecurityCode.RESET_PASSWORD_SUCCESS);
        return ResponseEntity
                .status(body.getStatus())
                .body(body);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        userService.withdraw(principalDetails.getUserId());
        ApiResponse<Void> body = ApiResponse.ok(UserCode.USER_WITHDRAWAL_SUCCESS);
        return ResponseEntity
                .status(body.getStatus())
                .body(body);
    }
}
