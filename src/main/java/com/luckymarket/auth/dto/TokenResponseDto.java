package com.luckymarket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    @Schema(description = "유저 아이디", example = "12345")
    private Long userId;

    @Schema(description = "액세스 토큰", example = "Bearer <access_token>")
    private String accessToken;

    public TokenResponseDto(Long userId, String accessToken) {
        this.userId = userId;
        this.accessToken = "Bearer " + accessToken;
    }
}
