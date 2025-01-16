package com.luckymarket.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtTokenDto {
    @Schema(description = "액세스 토큰", example = "Bearer <access_token>")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "<refresh_token>")
    private String refreshToken;
}
