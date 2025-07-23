package com.itplace.userapi.security.auth.oauth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoCodeRequest {
    private String code;
}
