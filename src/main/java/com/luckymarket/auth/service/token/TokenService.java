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

    public TokenResponseDto refreshAccessToken(String accessToken) {
        jwtTokenProvider.validateToken(accessToken);

        Long userId = Long.parseLong(jwtTokenProvider.getSubject(accessToken));
        String refreshToken = redisService.getRefreshToken(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        jwtTokenProvider.validateToken(refreshToken);

        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        redisService.addToBlacklist(accessToken, jwtTokenProvider.getRemainingExpirationTime(accessToken));
        redisService.deleteRefreshToken(userId);
        redisService.saveRefreshToken(userId, newRefreshToken, jwtTokenProvider.getRemainingExpirationTime(newRefreshToken));

        return new TokenResponseDto(newAccessToken);
    }
}