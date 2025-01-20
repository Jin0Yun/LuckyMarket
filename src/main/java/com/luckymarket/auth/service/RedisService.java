package com.luckymarket.auth.service;

import java.util.Optional;

public interface RedisService {
    void saveRefreshToken(Long userId, String refreshToken, long expiration);
    Optional<String> getRefreshToken(Long userId);
    void deleteRefreshToken(Long userId);
    void deleteBlacklistToken(String token);
    void addToBlacklist(String token, long expiration);
    boolean isBlacklisted(String token);
    boolean isKeyExist(String key);
    boolean isUserLoggedIn(Long userId);
}
