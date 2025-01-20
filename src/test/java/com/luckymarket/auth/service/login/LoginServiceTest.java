package com.luckymarket.auth.service.login;

import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.service.redis.RedisService;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.repository.UserRepository;
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

        when(userRepository.findByEmail(email)).thenReturn(null);

        // when & then
        assertThrows(AuthException.class, () -> loginService.login(email, password));
    }

    @DisplayName("이메일이 빈 값일 경우 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        // given
        String blankEmail = "";
        String validPassword = "Password123!";

        // when & then
        assertThrows(AuthException.class, () -> loginService.login(blankEmail, validPassword));  // 이메일 빈 값 예외
    }

    @DisplayName("이메일 형식이 잘못된 경우 로그인 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailIsInvalidFormat() {
        // given
        String invalidEmail = "invalid-email";
        String validPassword = "Password123!";

        // when & then
        assertThrows(AuthException.class, () -> loginService.login(invalidEmail, validPassword));  // 잘못된 이메일 형식 예외
    }

    @DisplayName("비밀번호가 빈 값일 경우 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        // given
        String validEmail = "luckymarket@gmail.com";
        String blankPassword = "";

        // when & then
        assertThrows(AuthException.class, () -> loginService.login(validEmail, blankPassword));  // 비밀번호 빈 값 예외
    }

    @DisplayName("잘못된 비밀번호로 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // given
        String email = "luckymarket@gmail.com";
        String password = "wrongPassword";

        when(userRepository.findByEmail(email)).thenReturn(mockMember);
        when(passwordEncoder.matches(password, mockMember.getPassword())).thenReturn(false);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> loginService.login(email, password));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("로그인 성공 시 JWT 토큰을 반환하는지 테스트")
    @Test
    void shouldReturnJwtTokenWhenLoginIsSuccessful() {
        // given
        String email = "luckymarket@gmail.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(mockMember);
        when(passwordEncoder.matches(password, mockMember.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createAccessToken(mockMember.getId())).thenReturn("mockAccessToken");

        // when
        TokenResponseDto result = loginService.login(email, password);

        // then
        assertThat(result.getAccessToken()).isEqualTo("Bearer mockAccessToken");
    }
}
