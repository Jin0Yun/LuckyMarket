package com.luckymarket.security;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String email) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + EXPIRATION_TIME);
        log.debug("토큰 생성: 이메일 = {}, 만료시간 = {}", email, accessTokenExpiresIn);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken() {
        long now = (new Date()).getTime();
        Date refreshTokenExpiresIn = new Date(now + EXPIRATION_TIME * 24);
        log.debug("리프레시 토큰 생성: 만료시간 = {}", refreshTokenExpiresIn);

        return Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            log.info("토큰 검증 성공");
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 토큰입니다.");
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            log.warn("잘못된 토큰 형식입니다.");
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        } catch (SignatureException e) {
            log.warn("JWT 서명이 일치하지 않습니다.");
            throw new AuthException(AuthErrorCode.TOKEN_SIGNATURE_INVALID);
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다.");
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("토큰을 파싱하는 중 오류가 발생했습니다.");
            throw new AuthException(AuthErrorCode.TOKEN_PARSING_FAILED);
        }
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        User principal = new User(email, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}
