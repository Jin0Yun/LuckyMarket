package com.luckymarket.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "회원 비밀번호 업데이트 요청 DTO")
public class UserPasswordUpdateRequest {
    @Schema(description = "회원의 새로운 비밀번호", example = "newPassword123!", required = true)
    private String password;
}
