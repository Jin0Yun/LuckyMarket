package com.luckymarket.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignupRequestDto {
    @Schema(description = "사용자 이메일", example = "example@email.com", required = true)
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "Password123!", minLength = 8, required = true)
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @Schema(description = "사용자 이름", example = "username", required = true)
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String username;
}