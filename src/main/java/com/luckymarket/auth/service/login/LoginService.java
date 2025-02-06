package com.luckymarket.auth.service.login;

import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.dto.LoginResponseDto;
import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.service.redis.RedisService;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.model.Status;
import com.luckymarket.user.domain.repository.UserRepository;
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

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        loginValidator.validateEmail(loginRequestDto.getEmail());
        loginValidator.validatePassword(loginRequestDto.getPassword());

        Member member = userRepository.findByEmail(loginRequestDto.getEmail());
        if (member == null) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_FOUND);
        }

        if (member.getStatus() == Status.DELETED) {
            throw new AuthException(AuthErrorCode.USER_ALREADY_DELETED);
        }

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
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
        return new LoginResponseDto(accessToken, refreshToken);
    }
}

