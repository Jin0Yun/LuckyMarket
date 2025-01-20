package com.luckymarket.auth.security;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProviderImpl implements JwtTokenProvider {
    private final Key key;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15;
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7;

    public JwtTokenProviderImpl(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(Long userId, long expiration) {
        long now = System.currentTimeMillis();
        Date expiryDate = new Date(now + expiration);

        JwtBuilder builder = Jwts.builder()
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256);

        if (userId != null) {
            builder.setSubject(String.valueOf(userId));
        }

        return builder.compact();
    }

    @Override
    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS_TOKEN_EXPIRATION);
    }

    @Override
    public String createRefreshToken(Long userId) {
        return createToken(userId, REFRESH_TOKEN_EXPIRATION);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new AuthException(AuthErrorCode.MALFORMED_TOKEN);
        } catch (SignatureException e) {
            throw new AuthException(AuthErrorCode.TOKEN_SIGNATURE_INVALID);
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public Authentication getAuthentication(String token) {
        Long userId = Long.parseLong(getSubject(token));
        return new UsernamePasswordAuthenticationToken(userId, token, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public long getRemainingExpirationTime(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }
}
