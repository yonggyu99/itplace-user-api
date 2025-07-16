package com.itplace.userapi.security.auth.local.dto.response;

import com.itplace.userapi.benefit.entity.enums.Grade;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginWithTokenResponse {

    String accessToken;
    String refreshToken;
    String name;
    Grade membershipGrade;
}
