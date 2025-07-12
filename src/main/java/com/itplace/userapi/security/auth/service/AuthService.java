package com.itplace.userapi.security.auth.service;

import com.itplace.userapi.security.auth.dto.request.LoginRequest;
import com.itplace.userapi.security.auth.dto.request.SignUpRequest;
import com.itplace.userapi.security.auth.dto.response.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request);

    void logout(Long userId);

    void signUp(SignUpRequest request);
}
