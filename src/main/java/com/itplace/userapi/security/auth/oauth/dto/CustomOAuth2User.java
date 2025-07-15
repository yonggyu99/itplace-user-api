package com.itplace.userapi.security.auth.oauth.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    @Getter
    private final OAuth2InfoResponse oAuth2InfoResponse;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority(oAuth2InfoResponse.getRole().name()));
        return collection;
    }

    @Override
    public String getName() {
        return oAuth2InfoResponse.getProviderId();
    }

    public String getEmail() {
        return oAuth2InfoResponse.getEmail();
    }

    public String getProvider() {
        return oAuth2InfoResponse.getProvider();
    }

    public String getProviderId() {
        return oAuth2InfoResponse.getProviderId();
    }
}
