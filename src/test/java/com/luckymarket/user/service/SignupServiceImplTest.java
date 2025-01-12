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

class SignupServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SignupServiceImpl signupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("회원가입 시 비밀번호가 8자리 이상인지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsLessThan8Characters() {
        // given
        Member member = new Member();
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
        member.setPassword(null);

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_BLANK.getMessage());
    }
}
