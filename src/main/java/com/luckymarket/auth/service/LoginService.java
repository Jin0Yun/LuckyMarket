package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final AuthValidator authValidator;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RedisService redisService, AuthValidator authValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.authValidator = authValidator;
    }

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

        if (redisService.isUserLoggedIn(member.getId())) {
            throw new AuthException(AuthErrorCode.ALREADY_LOGGED_IN_OTHER_DEVICE);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        redisService.saveRefreshToken(member.getId(), refreshToken, jwtTokenProvider.getRemainingExpirationTime(refreshToken));

        return new TokenResponseDto(accessToken);
    }
}

