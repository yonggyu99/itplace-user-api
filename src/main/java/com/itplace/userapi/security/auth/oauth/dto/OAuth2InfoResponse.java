package com.itplace.userapi.security.auth.oauth.dto;

import com.itplace.userapi.user.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2InfoResponse {

    private String registrationId;
    private Long userId;
    private String provider;
    private String providerId;
    private String email;
    private Role role;
}
