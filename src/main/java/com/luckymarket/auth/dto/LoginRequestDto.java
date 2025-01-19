package com.luckymarket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    @Schema(description = "사용자 이메일", example = "example@email.com", required = true)
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "Password123!", minLength = 8, required = true)
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
