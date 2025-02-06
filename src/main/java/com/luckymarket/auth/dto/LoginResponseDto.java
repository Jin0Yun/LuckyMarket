package com.luckymarket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    @Schema(description = "액세스 토큰", example = "Bearer <access_token>")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "<refresh_token>")
    private String refreshToken;
}
