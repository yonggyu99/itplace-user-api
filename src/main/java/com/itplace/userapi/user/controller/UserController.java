package com.itplace.userapi.user.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.verification.email.dto.EmailConfirmRequest;
import com.itplace.userapi.security.verification.email.dto.EmailVerificationRequest;
import com.itplace.userapi.security.verification.email.service.EmailService;
import com.itplace.userapi.security.verification.sms.dto.SmsVerificationRequest;
import com.itplace.userapi.security.verification.sms.service.SmsService;
import com.itplace.userapi.user.UserCode;
import com.itplace.userapi.user.dto.UserInfoDto;
import com.itplace.userapi.user.dto.request.FindEmailConfirmRequest;
import com.itplace.userapi.user.dto.response.FindEmailResponse;
import com.itplace.userapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SmsService smsService;
    private final EmailService emailService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getUser(@PathVariable Long userId) {
        UserInfoDto userInfoDto = userService.getUserInfo(userId);
        ApiResponse<?> body = ApiResponse.of(UserCode.USER_INFO_SUCCESS, userInfoDto);

        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @PostMapping("/findEmail")
    public ResponseEntity<ApiResponse<Void>> findEmail(SmsVerificationRequest request) {
        smsService.send(request);
        ApiResponse<Void> body = ApiResponse.ok(SecurityCode.SMS_SEND_SUCCESS);
        return new ResponseEntity<>(body, body.getStatus());
    }

    @PostMapping("/findEmail/confirm")
    public ResponseEntity<ApiResponse<FindEmailResponse>> findEmailConfirm(FindEmailConfirmRequest request) {
        FindEmailResponse response = userService.findEmailConfirm(request);
        ApiResponse<FindEmailResponse> body = ApiResponse.of(UserCode.EMAIL_FIND_SUCCESS, response);
        return new ResponseEntity<>(body, body.getStatus());
    }

    @PostMapping("/findPassword")
    public ResponseEntity<ApiResponse<Void>> findPassword(EmailVerificationRequest request) {
        emailService.send(request);
        ApiResponse<Void> body = ApiResponse.ok(SecurityCode.EMAIL_SEND_SUCCESS);
        return new ResponseEntity<>(body, body.getStatus());
    }

    @PostMapping("/findPassword/confirm")
    public ResponseEntity<ApiResponse<FindEmailResponse>> findPasswordConfirm(EmailConfirmRequest request) {
        userService.findPasswordConfirm(request);
        ApiResponse<FindEmailResponse> body = ApiResponse.ok(SecurityCode.EMAIL_VERIFICATION_SUCCESS);
        return new ResponseEntity<>(body, body.getStatus());
    }

}
