package com.luckymarket.infrastructure.redis;

import com.luckymarket.domain.exception.auth.RedisErrorCode;
import com.luckymarket.domain.exception.auth.RedisException;
import com.luckymarket.domain.utils.RedisKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveRefreshToken(Long userId, String refreshToken, long expirationTime) {
        String key = RedisKeyUtils.getRefreshTokenKey(userId);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
        if (!Boolean.TRUE.equals(success)) {
            throw new RedisException(RedisErrorCode.REFRESH_TOKEN_SAVE_FAILED);
        }
    }

    @Override
    public Optional<String> getRefreshToken(Long userId) {
        String key = RedisKeyUtils.getRefreshTokenKey(userId);
        String token = redisTemplate.opsForValue().get(key);
        if (token == null || token.trim().isEmpty()) {
            throw new RedisException(RedisErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        return Optional.of(token);
    }

    @Override
    public void removeRefreshToken(Long userId) {
        String key = RedisKeyUtils.getRefreshTokenKey(userId);
        boolean deleted = redisTemplate.delete(key);
        if (!deleted) {
            throw new RedisException(RedisErrorCode.REFRESH_TOKEN_DELETE_FAILED);
        }
    }

    @Override
    public void addToBlacklist(String accessToken, long expirationTime) {
        String key = RedisKeyUtils.getBlacklistKey(accessToken);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "BLACKLISTED", expirationTime, TimeUnit.MILLISECONDS);
        if (!Boolean.TRUE.equals(success)) {
            throw new RedisException(RedisErrorCode.BLACKLIST_TOKEN_SAVE_FAILED);
        }
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        String key = RedisKeyUtils.getBlacklistKey(accessToken);
        if (!redisTemplate.hasKey(key)) {
            throw new RedisException(RedisErrorCode.BLACKLIST_TOKEN_NOT_FOUND);
        }
        return redisTemplate.hasKey(key);
    }

    @Override
    public void removeFromBlacklist(String accessToken) {
        String key = RedisKeyUtils.getBlacklistKey(accessToken);
        boolean deleted = redisTemplate.delete(key);
        if (!deleted) {
            throw new RedisException(RedisErrorCode.BLACKLIST_TOKEN_DELETE_FAILED);
        }
    }

    @Override
    public boolean isUserLoggedIn(Long userId) {
        String key = RedisKeyUtils.getUserLoggedInKey(userId);
        return redisTemplate.hasKey(key);
    }

    @Override
    public void markUserAsLoggedIn(Long userId) {
        String key = RedisKeyUtils.getUserLoggedInKey(userId);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "true");
        if (!Boolean.TRUE.equals(success)) {
            throw new RedisException(RedisErrorCode.KEY_SAVE_FAILED);
        }
    }

    @Override
    public void markUserAsLoggedOut(Long userId) {
        String key = RedisKeyUtils.getUserLoggedInKey(userId);
        boolean deleted = redisTemplate.delete(key);
        if (!deleted) {
            throw new RedisException(RedisErrorCode.KEY_DELETE_FAILED);
        }
    }
}