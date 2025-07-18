package com.itplace.userapi.security.auth.local.service;

import com.itplace.userapi.security.auth.local.dto.request.LoginRequest;
import com.itplace.userapi.security.auth.local.dto.request.SignUpRequest;
import com.itplace.userapi.security.auth.local.dto.request.UplusDataRequest;
import com.itplace.userapi.security.auth.local.dto.response.UplusDataResponse;
import java.util.Optional;

public interface AuthService {

    void login(LoginRequest request);

    void logout(Long userId);

    void signUp(SignUpRequest request);

    Optional<UplusDataResponse> uplusData(UplusDataRequest request);
}
