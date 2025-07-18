package com.itplace.userapi.security.auth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itplace.userapi.common.BaseCode;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.auth.local.dto.response.TokenResponse;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuth2LoginResult {

    private final BaseCode code;
    private final TokenResponse tokens;
    private final OAuth2InfoResponse oAuth2InfoResponse;

    private OAuth2LoginResult(SecurityCode securityCode, TokenResponse tokens, OAuth2InfoResponse oAuth2InfoResponse) {
        this.code = securityCode;
        this.tokens = tokens;
        this.oAuth2InfoResponse = oAuth2InfoResponse;
    }

    public static OAuth2LoginResult success(TokenResponse tokenResponse) {
        return new OAuth2LoginResult(SecurityCode.LOGIN_SUCCESS, tokenResponse, null);
    }

    public static OAuth2LoginResult signupRequired(OAuth2InfoResponse oAuth2InfoResponse) {
        return new OAuth2LoginResult(SecurityCode.SIGNUP_REQUIRED, null, oAuth2InfoResponse);
    }
}
