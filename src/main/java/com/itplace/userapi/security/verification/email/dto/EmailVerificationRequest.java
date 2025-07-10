package com.itplace.userapi.security.verification.email.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationRequest {

    @Email(message = "이메일 형식을 맞춰주세요")
    String email;
}
