package com.luckymarket.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원의 비밀번호 업데이트를 위한 DTO")
public class PasswordUpdateDto {
    @Schema(description = "회원의 새로운 비밀번호", example = "newPassword123!", required = true)
    private String password;
}
