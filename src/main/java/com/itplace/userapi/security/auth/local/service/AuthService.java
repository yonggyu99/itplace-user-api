package com.itplace.userapi.security.auth.local.service;

import com.itplace.userapi.security.auth.local.dto.request.LoginRequest;
import com.itplace.userapi.security.auth.local.dto.request.SignUpRequest;
import com.itplace.userapi.security.auth.local.dto.response.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request);

    void logout(Long userId);

    void signUp(SignUpRequest request);
}
