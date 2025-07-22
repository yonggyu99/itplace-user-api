package com.itplace.userapi.security.auth.local.service;

import com.itplace.userapi.security.auth.local.dto.request.SignUpRequest;
import com.itplace.userapi.security.auth.local.dto.request.UplusDataRequest;
import com.itplace.userapi.security.auth.local.dto.response.UplusDataResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface AuthService {

    void reissue(HttpServletRequest request, HttpServletResponse response);

    void logout(Long userId);

    void signUp(SignUpRequest request);

    Optional<UplusDataResponse> uplusData(UplusDataRequest request);
}
