package com.luckymarket.auth.service.token;

import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.service.redis.RedisService;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public TokenService(JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    public TokenResponseDto refreshAccessToken(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);

        Long userId = Long.parseLong(jwtTokenProvider.getSubject(refreshToken));
        String redisRefreshToken = redisService.getRefreshToken(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        if (!redisRefreshToken.equals(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        return new TokenResponseDto(newAccessToken);
    }
}