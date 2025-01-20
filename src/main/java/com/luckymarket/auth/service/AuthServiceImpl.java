package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.exception.RedisErrorCode;
import com.luckymarket.auth.exception.RedisException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.user.domain.Member;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final AuthValidator authValidator;

    @Autowired
    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            RedisService redisService,
            AuthValidator authValidator
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.authValidator = authValidator;
    }

    @Override
    public TokenResponseDto login(String email, String password) {
        validateLoginRequest(email, password);

        Member member = findMemberByEmail(email);
        verifyPassword(password, member);

        if (redisService.isUserLoggedIn(member.getId())) {
            throw new AuthException(AuthErrorCode.ALREADY_LOGGED_IN_OTHER_DEVICE);
        }

        String accessToken = createAccessToken(member);
        String refreshToken = createRefreshToken(member);

        saveRefreshTokenToRedis(member, refreshToken);
        return new TokenResponseDto(accessToken);
    }

    @Override
    public void logout(String accessToken) {
        String token = accessToken.replace("Bearer ", "").trim();
        Long userId = Long.parseLong(jwtTokenProvider.getSubject(token));

        if (redisService.isBlacklisted(token)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        redisService.addToBlacklist(token, jwtTokenProvider.getRemainingExpirationTime(token));
        redisService.deleteRefreshToken(userId);
    }

    @Override
    public TokenResponseDto refreshAccessToken(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        Long userId = Long.parseLong(jwtTokenProvider.getSubject(accessToken));
        String currentRefreshToken = redisService.getRefreshToken(userId).orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        if (!jwtTokenProvider.validateToken(currentRefreshToken)) {
            throw new RedisException(RedisErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        redisService.addToBlacklist(accessToken, jwtTokenProvider.getRemainingExpirationTime(accessToken));
        redisService.deleteRefreshToken(userId);
        redisService.saveRefreshToken(userId, refreshToken, jwtTokenProvider.getRemainingExpirationTime(refreshToken));

        return new TokenResponseDto(newAccessToken);
    }


    private void validateLoginRequest(String email, String password) {
        authValidator.validateEmail(email);
        authValidator.validatePassword(password);
    }

    private Member findMemberByEmail(String email) {
        Member member = userRepository.findByEmail(email);
        if (member == null) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_FOUND);
        }
        return member;
    }

    private void verifyPassword(String password, Member member) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new AuthException(AuthErrorCode.PASSWORD_MISMATCH);
        }
    }

    private String createAccessToken(Member member) {
        return jwtTokenProvider.createAccessToken(member.getId());
    }

    private String createRefreshToken(Member member) {
        return jwtTokenProvider.createRefreshToken(member.getId());
    }

    private void saveRefreshTokenToRedis(Member member, String refreshToken) {
        redisService.saveRefreshToken(member.getId(), refreshToken, jwtTokenProvider.getRemainingExpirationTime(refreshToken));
    }
}
