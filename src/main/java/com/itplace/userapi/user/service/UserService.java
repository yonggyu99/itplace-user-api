package com.itplace.userapi.user.service;

import com.itplace.userapi.security.verification.email.dto.EmailConfirmRequest;
import com.itplace.userapi.user.dto.UserInfoDto;
import com.itplace.userapi.user.dto.request.FindEmailConfirmRequest;
import com.itplace.userapi.user.dto.response.FindEmailResponse;

public interface UserService {
    UserInfoDto getUserInfo(Long userId);

    FindEmailResponse findEmailConfirm(FindEmailConfirmRequest request);

    void findPasswordConfirm(EmailConfirmRequest request);
}