package com.itplace.userapi.user.service;

import com.itplace.userapi.security.auth.dto.request.SignUpRequest;

public interface UserService {

    void signUp(SignUpRequest request);
}