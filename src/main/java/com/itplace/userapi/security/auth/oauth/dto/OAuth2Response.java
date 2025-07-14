package com.itplace.userapi.security.auth.oauth.dto;

public interface OAuth2Response {

    String getProvider();

    String getProviderId();

    String getEmail();
}
