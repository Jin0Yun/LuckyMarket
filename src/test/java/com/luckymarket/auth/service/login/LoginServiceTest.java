package com.luckymarket.auth.service.login;

import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.dto.LoginResponseDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.service.redis.RedisService;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginValidator loginValidator;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private LoginService loginService;

    private Member mockMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMember = Member.builder()
                .email("luckymarket@gmail.com")
                .password("Password123!")
                .username("username")
                .build();
    }

    @DisplayName("존재하지 않는 이메일로 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {
        // given
        String email = "nonexistent@example.com";
        String password = "password";
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        when(userRepository.findByEmail(email)).thenReturn(null);

        // when & then
        assertThrows(AuthException.class, () -> loginService.login(loginRequestDto));
    }

    @DisplayName("이메일이 빈 값일 경우 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        // given
        String blankEmail = "";
        String validPassword = "Password123!";
        LoginRequestDto loginRequestDto = new LoginRequestDto(blankEmail, validPassword);

        // when & then
        assertThrows(AuthException.class, () -> loginService.login(loginRequestDto));
    }

    @DisplayName("이메일 형식이 잘못된 경우 로그인 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailIsInvalidFormat() {
        // given
        String invalidEmail = "invalid-email";
        String validPassword = "Password123!";
        LoginRequestDto loginRequestDto = new LoginRequestDto(invalidEmail, validPassword);

        // when & then
        assertThrows(AuthException.class, () -> loginService.login(loginRequestDto));
    }

    @DisplayName("비밀번호가 빈 값일 경우 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        // given
        String validEmail = "luckymarket@gmail.com";
        String blankPassword = "";
        LoginRequestDto loginRequestDto = new LoginRequestDto(validEmail, blankPassword);

        // when & then
        assertThrows(AuthException.class, () -> loginService.login(loginRequestDto));
    }

    @DisplayName("잘못된 비밀번호로 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // given
        String email = "luckymarket@gmail.com";
        String password = "wrongPassword";
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        when(userRepository.findByEmail(email)).thenReturn(mockMember);
        when(passwordEncoder.matches(password, mockMember.getPassword())).thenReturn(false);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> loginService.login(loginRequestDto));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("로그인 성공 시 JWT 토큰을 반환하는지 테스트")
    @Test
    void shouldReturnJwtTokenWhenLoginIsSuccessful() {
        // given
        String email = "luckymarket@gmail.com";
        String password = "password";
        LoginRequestDto loginRequestDto = new LoginRequestDto(email, password);

        when(userRepository.findByEmail(email)).thenReturn(mockMember);
        when(passwordEncoder.matches(password, mockMember.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createAccessToken(mockMember.getId())).thenReturn("mockAccessToken");
        when(jwtTokenProvider.createRefreshToken(mockMember.getId())).thenReturn("mockRefreshToken");

        // when
        LoginResponseDto result = loginService.login(loginRequestDto);

        // then
        assertThat(result.getAccessToken()).isEqualTo("mockAccessToken");
        assertThat(result.getRefreshToken()).isEqualTo("mockRefreshToken");
    }
}
