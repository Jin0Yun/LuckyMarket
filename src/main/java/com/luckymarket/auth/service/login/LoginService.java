package com.luckymarket.auth.service.login;

import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.service.redis.RedisService;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final LoginValidator loginValidator;

    public LoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RedisService redisService, LoginValidator loginValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.loginValidator = loginValidator;
    }

    public TokenResponseDto login(String email, String password) {
        loginValidator.validateEmail(email);
        loginValidator.validatePassword(password);

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
        member.setLastLogin(LocalDateTime.now());
        userRepository.save(member);
        return new TokenResponseDto(accessToken);
    }
}

