package com.itplace.userapi.security.auth.oauth.dto;

import com.itplace.userapi.user.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2InfoResponse {

    private String provider;
    private String providerId;
    private String email;
    private Role role;
}
