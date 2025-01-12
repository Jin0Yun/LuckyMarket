package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.exception.SignupErrorCode;
import com.luckymarket.user.exception.SignupException;
import com.luckymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class SignupServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SignupServiceImpl signupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("회원가입 시 이메일이 공백인지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        // given
        Member member = new Member();
        member.setEmail(" ");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.EMAIL_BLANK.getMessage());
    }

    @DisplayName("회원가입 시 이메일이 누락되었는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenEmailIsMissing() {
        // given
        Member member = new Member();
        member.setEmail(null);

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.EMAIL_BLANK.getMessage());
    }

    @DisplayName("회원가입 시 이메일이 이미 존재하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // given
        Member member = new Member();
        member.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.EMAIL_ALREADY_USED.getMessage());
    }

    @DisplayName("회원가입 시 잘못된 이메일 형식인지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        // given
        Member member = new Member();
        member.setEmail("invalid-email");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호가 8자리 이상인지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsLessThan8Characters() {
        // given
        Member member = new Member();
        member.setEmail("test@example.com");
        member.setPassword("short");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_TOO_SHORT.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호에 대문자가 포함되어 있는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordDoesNotContainUppercaseLetter() {
        // given
        Member member = new Member();
        member.setEmail("test@example.com");
        member.setPassword("lowercase123");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_MISSING_UPPERCASE.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호에 소문자가 포함되어 있는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordDoesNotContainLowercaseLetter() {
        // given
        Member member = new Member();
        member.setEmail("test@example.com");
        member.setPassword("UPPERCASE123");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_MISSING_LOWERCASE.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호에 특수문자가 포함되어 있는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordDoesNotContainSpecialCharacter() {
        // given
        Member member = new Member();
        member.setEmail("test@example.com");
        member.setPassword("Password123");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_MISSING_SPECIAL_CHAR.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호가 공백인지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        // given
        Member member = new Member();
        member.setEmail("test@example.com");
        member.setPassword(" ");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_BLANK.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호가 누락되었는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsMissing() {
        // given
        Member member = new Member();
        member.setEmail("test@example.com");
        member.setPassword(null);

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_BLANK.getMessage());
    }
}
