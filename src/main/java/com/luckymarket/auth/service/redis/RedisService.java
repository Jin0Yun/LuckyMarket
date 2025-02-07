package com.luckymarket.auth.service.redis;

import java.util.Optional;

public interface RedisService {
    void saveRefreshToken(Long userId, String refreshToken, long expiration);
    Optional<String> getRefreshToken(Long userId);
    void removeRefreshToken(Long userId);
    void addToBlacklist(String token, long expiration);
    boolean isBlacklisted(String token);
    void removeFromBlacklist(String accessToken);
    void markUserAsLoggedIn(Long userId);
    void markUserAsLoggedOut(Long userId);
}
