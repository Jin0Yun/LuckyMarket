package com.luckymarket.auth.service.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveRefreshToken(Long userId, String refreshToken, long expirationTime) {
        redisTemplate.opsForValue().setIfAbsent("user:refreshToken:" + userId, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public Optional<String> getRefreshToken(Long userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get("user:refreshToken:" + userId));
    }

    @Override
    public void removeRefreshToken(Long userId) {
        redisTemplate.delete("user:refreshToken:" + userId);
    }

    @Override
    public void addToBlacklist(String accessToken, long expirationTime) {
        redisTemplate.opsForValue().setIfAbsent(accessToken, "BLACKLISTED", expirationTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        return redisTemplate.hasKey(accessToken);
    }

    @Override
    public void removeFromBlacklist(String accessToken) {
        redisTemplate.delete(accessToken);
    }
}
