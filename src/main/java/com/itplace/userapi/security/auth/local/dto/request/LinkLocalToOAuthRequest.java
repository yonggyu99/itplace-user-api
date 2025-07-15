package com.itplace.userapi.security.auth.local.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkLocalToOAuthRequest {

    @NotBlank(message = "등록 ID는 필수 항목입니다.")
    String registrationId;

    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    String email;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 6, max = 30, message = "비밀번호는 6자 이상 30자 이하로 입력해주세요.")
    String password;

    @NotBlank(message = "비밀번호 확인은 필수 항목입니다.")
    @Size(min = 6, max = 30, message = "비밀번호는 6자 이상 30자 이하로 입력해주세요.")
    String passwordConfirm;
}
