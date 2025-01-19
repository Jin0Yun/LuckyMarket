package com.luckymarket.auth.service;

import com.luckymarket.auth.exception.RedisErrorCode;
import com.luckymarket.auth.exception.RedisException;
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
    public void saveRefreshToken(String userId, String refreshToken, long expiration) {
        try {
            Boolean isSet = redisTemplate.opsForValue().setIfAbsent("refresh:" + userId, refreshToken);
            if (isSet != null && isSet) {
                redisTemplate.expire("refresh:" + userId, expiration, TimeUnit.MILLISECONDS);
            } else {
                throw new RedisException(RedisErrorCode.REFRESH_TOKEN_SAVE_FAILED);
            }
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.REFRESH_TOKEN_SAVE_FAILED);
        }
    }

    @Override
    public Optional<String> getRefreshToken(String userId) {
        try {
            String token = redisTemplate.opsForValue().get("refresh:" + userId);
            return Optional.ofNullable(token);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteRefreshToken(String userId) {
        try {
            boolean result = redisTemplate.delete("refresh:" + userId);
            if (!result) {
                throw new RedisException(RedisErrorCode.REFRESH_TOKEN_DELETE_FAILED);
            }
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.REFRESH_TOKEN_DELETE_FAILED);
        }
    }

    @Override
    public void deleteBlacklistToken(String token) {
        try {
            boolean result = redisTemplate.delete("blacklist:" + token);
            if (!result) {
                throw new RedisException(RedisErrorCode.BLACKLIST_TOKEN_DELETE_FAILED);
            }
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.BLACKLIST_TOKEN_DELETE_FAILED);
        }
    }

    @Override
    public void addToBlacklist(String token, long expiration) {
        try {
            redisTemplate.opsForValue().set("blacklist:" + token, "BLACKLISTED", expiration, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.BLACKLIST_TOKEN_SAVE_FAILED);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        try {
            return redisTemplate.hasKey("blacklist:" + token);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isKeyExist(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            return false;
        }
    }
}
