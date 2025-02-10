package com.luckymarket.infrastructure.redis;

import java.util.Optional;

public interface RedisService {
    void saveRefreshToken(Long userId, String refreshToken, long expiration);
    Optional<String> getRefreshToken(Long userId);
    void removeRefreshToken(Long userId);
    void addToBlacklist(String token, long expiration);
    boolean isBlacklisted(String token);
    void removeFromBlacklist(String accessToken);
    boolean isUserLoggedIn(Long userId);
    void markUserAsLoggedIn(Long userId);
    void markUserAsLoggedOut(Long userId);
}
