package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.security.JwtTokenProviderImpl;
import com.luckymarket.auth.service.login.LoginValidator;
import com.luckymarket.auth.service.redis.RedisServiceImpl;
import com.luckymarket.user.domain.Member;
import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    @Mock
    private JwtTokenProviderImpl jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private LoginValidator loginValidator;

    @Mock
    private RedisServiceImpl redisServiceImpl;

    @InjectMocks
    private AuthServiceImpl authService;

    private Member mockMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 회원 정보 초기화
        mockMember = Member.builder()
                .email("luckymarket@gmail.com")
                .password("LuckyMarket123!!")
                .username("testuser")
                .build();
    }

    @DisplayName("존재하지 않는 이메일로 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("nonexistent@example.com");
        dto.setPassword("password");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(null);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(dto.getEmail(), dto.getPassword()));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.EMAIL_NOT_FOUND.getMessage());
    }

    @DisplayName("잘못된 비밀번호로 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("luckymarket@gmail.com");
        dto.setPassword("wrongPassword");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(mockMember);
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(false);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(dto.getEmail(), dto.getPassword()));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("이메일이 빈 값일 경우 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(" ");
        dto.setPassword("LuckyMarket123!!");

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(dto.getEmail(), dto.getPassword()));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.EMAIL_BLANK.getMessage());
    }

    @DisplayName("비밀번호가 빈 값일 경우 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("luckymarket@gmail.com");
        dto.setPassword(" ");

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(dto.getEmail(), dto.getPassword()));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.PASSWORD_BLANK.getMessage());
    }

    @DisplayName("이메일 형식이 잘못된 경우 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("invalid-email");
        dto.setPassword("LuckyMarket123!!");

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(dto.getEmail(), dto.getPassword()));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @DisplayName("로그인 성공 시 JWT 토큰을 반환하는지 테스트")
    @Test
    void shouldReturnUserWhenLoginIsSuccessful() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("luckymarket@gmail.com");
        dto.setPassword("LuckyMarket123!!");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(mockMember);
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(true);

        // when
        TokenResponseDto result = authService.login(dto.getEmail(), dto.getPassword());

        // then
        assertThat(result.getAccessToken()).startsWith("Bearer ");
    }

    @DisplayName("만료된 JWT 토큰으로 인증 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenTokenIsExpired() {
        // given
        String expiredToken = "expired-jwt-token";
        when(jwtTokenProvider.validateToken(expiredToken)).thenThrow(new AuthException(AuthErrorCode.EXPIRED_TOKEN));

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> jwtTokenProvider.validateToken(expiredToken));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.EXPIRED_TOKEN.getMessage());
    }

    @DisplayName("잘못된 서명을 가진 JWT 토큰 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenTokenHasInvalidSignature() {
        // given
        String invalidSignatureToken = "invalid-signature";
        when(jwtTokenProvider.validateToken(invalidSignatureToken)).thenThrow(new AuthException(AuthErrorCode.INVALID_TOKEN));

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> jwtTokenProvider.validateToken(invalidSignatureToken));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.INVALID_TOKEN.getMessage());
    }

    @DisplayName("잘못된 형식의 JWT 토큰 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenTokenHasInvalidFormat() {
        // given
        String invalidFormatToken = "invalid-format";
        when(jwtTokenProvider.validateToken(invalidFormatToken)).thenThrow(new AuthException(AuthErrorCode.INVALID_TOKEN));

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> jwtTokenProvider.validateToken(invalidFormatToken));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.INVALID_TOKEN.getMessage());
    }

    @DisplayName("로그아웃 성공 시 토큰이 블랙리스트에 추가되고 리프레시 토큰이 삭제되는지 테스트")
    @Test
    void shouldBlacklistTokenAndDeleteRefreshTokenWhenLogoutIsSuccessful() {
        // given
        String accessToken = "Bearer valid-jwt-token";
        String token = "valid-jwt-token";
        Long userId = 1L;

        when(jwtTokenProvider.getSubject(token)).thenReturn(userId.toString());
        when(redisServiceImpl.isBlacklisted(token)).thenReturn(false);

        // when
        authService.logout(accessToken);

        // then
        verify(redisServiceImpl).addToBlacklist(token, jwtTokenProvider.getRemainingExpirationTime(token));
        verify(redisServiceImpl).deleteRefreshToken(userId);
    }

    @DisplayName("이미 블랙리스트에 있는 토큰으로 로그아웃 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenTokenIsAlreadyBlacklistedDuringLogout() {
        // given
        String accessToken = "Bearer blacklisted-jwt-token";
        String token = "blacklisted-jwt-token";
        Long userId = 1L;

        when(jwtTokenProvider.getSubject(token)).thenReturn(userId.toString());
        when(redisServiceImpl.isBlacklisted(token)).thenReturn(true);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.logout(accessToken));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.INVALID_TOKEN.getMessage());

        verify(redisServiceImpl, never()).addToBlacklist(token, jwtTokenProvider.getRemainingExpirationTime(token));
        verify(redisServiceImpl, never()).deleteRefreshToken(userId);
    }
}
