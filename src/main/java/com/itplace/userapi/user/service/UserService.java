package com.itplace.userapi.user.service;

import com.itplace.userapi.security.verification.email.dto.EmailConfirmRequest;
import com.itplace.userapi.user.dto.request.FindEmailConfirmRequest;
import com.itplace.userapi.user.dto.request.ResetPasswordRequest;
import com.itplace.userapi.user.dto.response.FindEmailResponse;
import com.itplace.userapi.user.dto.response.FindPasswordConfirmResponse;
import com.itplace.userapi.user.dto.response.UserInfoResponse;

public interface UserService {
    UserInfoResponse getUserInfo(Long userId);

    FindEmailResponse findEmailConfirm(FindEmailConfirmRequest request);

    FindPasswordConfirmResponse findPasswordConfirm(EmailConfirmRequest request);

    void resetPassword(ResetPasswordRequest request);
}