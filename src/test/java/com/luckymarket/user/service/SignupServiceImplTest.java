package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo("비밀번호는 최소 8자 이상이어야 합니다.");
    }

    @DisplayName("회원가입 시 비밀번호에 대문자가 포함되어 있는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordDoesNotContainUppercaseLetter() {
        // given
        Member member = new Member();
        member.setPassword("lowercase123");

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo("비밀번호는 최소 1개의 대문자를 포함해야 합니다.");
    }

    @DisplayName("회원가입 시 비밀번호에 소문자가 포함되어 있는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordDoesNotContainLowercaseLetter() {
        // given
        Member member = new Member();
        member.setPassword("UPPERCASE123");

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo("비밀번호는 최소 1개의 소문자를 포함해야 합니다.");
    }

    @DisplayName("회원가입 시 비밀번호에 특수문자가 포함되어 있는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordDoesNotContainSpecialCharacter() {
        // given
        Member member = new Member();
        member.setPassword("Password123");

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo("비밀번호는 최소 1개의 특수문자를 포함해야 합니다.");
    }

    @DisplayName("회원가입 시 비밀번호가 공백인지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        // given
        Member member = new Member();
        member.setPassword(" ");

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo("비밀번호는 필수 항목입니다.");
    }

    @DisplayName("회원가입 시 비밀번호가 누락되었는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsMissing() {
        // given
        Member member = new Member();
        member.setPassword(null);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> signupService.signup(member));
        assertThat(exception.getMessage()).isEqualTo("비밀번호는 필수 항목입니다.");
    }
}
