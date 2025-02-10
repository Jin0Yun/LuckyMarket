package com.luckymarket.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenResponseDto {
    @Schema(description = "새로운 액세스 토큰", example = "<new_access_token>")
    private String accessToken;
}
