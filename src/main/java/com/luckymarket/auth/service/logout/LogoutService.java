package com.luckymarket.auth.service.logout;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.service.redis.RedisService;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    public LogoutService(JwtTokenProvider jwtTokenProvider, RedisService redisService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
    }

    public void logout(String accessToken) {
        String token = accessToken.replace("Bearer ", "").trim();
        Long userId = Long.parseLong(jwtTokenProvider.getSubject(token));

        if (redisService.isBlacklisted(token)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        redisService.addToBlacklist(token, jwtTokenProvider.getRemainingExpirationTime(token));
        redisService.markUserAsLoggedOut(userId);
        redisService.removeRefreshToken(userId);
    }
}
