package com.itplace.userapi.security.verification.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EmailVerificationRequest {

    @NotBlank(message = "등록 ID를 입력해주세요.")
    private String registrationId;

    @Email(message = "이메일 형식을 맞춰주세요")
    private String email;
}
