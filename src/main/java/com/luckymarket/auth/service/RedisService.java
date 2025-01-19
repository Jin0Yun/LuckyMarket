package com.luckymarket.auth.service;

import java.util.Optional;

public interface RedisService {
    void saveRefreshToken(String userId, String refreshToken, long expiration);
    Optional<String> getRefreshToken(String userId);
    void deleteRefreshToken(String userId);
    void deleteBlacklistToken(String token);
    void addToBlacklist(String token, long expiration);
    boolean isBlacklisted(String token);
    boolean isKeyExist(String key);
}
