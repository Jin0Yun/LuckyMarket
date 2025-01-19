package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.TokenResponseDto;
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
        authValidator.validateEmail(email);
        authValidator.validatePassword(password);

        Member member = userRepository.findByEmail(email);
        if (member == null) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_FOUND);
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new AuthException(AuthErrorCode.PASSWORD_MISMATCH);
        }

        String accessToken = createAccessToken(member);
        String refreshToken = createRefreshToken(member);

        saveRefreshTokenToRedis(member, refreshToken);
        return new TokenResponseDto(accessToken);
    }

    private String createAccessToken(Member member) {
        return jwtTokenProvider.createAccessToken(String.valueOf(member.getId()));
    }

    private String createRefreshToken(Member member) {
        return jwtTokenProvider.createRefreshToken(String.valueOf(member.getId()));
    }

    private void saveRefreshTokenToRedis(Member member, String refreshToken) {
        redisService.saveRefreshToken(String.valueOf(member.getId()), refreshToken, jwtTokenProvider.getRefreshTokenExpiration());
    }
}
