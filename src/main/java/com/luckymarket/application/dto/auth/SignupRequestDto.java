package com.luckymarket.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SignupRequestDto {
    @Schema(description = "사용자 이메일", example = "example@email.com", required = true)
    private String email;

    @Schema(description = "사용자 비밀번호", example = "Password123!", minLength = 8, required = true)
    private String password;

    @Schema(description = "사용자 이름", example = "username", required = true)
    private String username;
}