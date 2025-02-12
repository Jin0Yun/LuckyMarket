package com.luckymarket.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "로그인 성공 시 반환되는 토큰 정보")
public class AuthTokenResponse {
    @Schema(description = "액세스 토큰", example = "Bearer <access_token>")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "<refresh_token>")
    private String refreshToken;
}
