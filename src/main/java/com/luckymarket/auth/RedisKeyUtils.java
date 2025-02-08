package com.luckymarket.auth;

public class RedisKeyUtils {
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:user: ";
    private static final String BLACKLIST_PREFIX = "blacklist: ";
    private static final String USER_LOGGED_IN_PREFIX = "user-logged-in: ";

    public static String getRefreshTokenKey(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }

    public static String getBlacklistKey(String token) {
        return BLACKLIST_PREFIX + token;
    }

    public static String getUserLoggedInKey(Long userId) {
        return USER_LOGGED_IN_PREFIX + userId;
    }
}
