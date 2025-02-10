package com.luckymarket.application.validation;

import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmailValidationRuleTest {
    private EmailValidationRule emailValidationRule;

    @BeforeEach
    void setUp() {
        emailValidationRule = new EmailValidationRule();
    }

    @DisplayName("이메일이 null이거나 빈 문자열일 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenEmailIsBlank() {
        // given
        String blankEmail = "";

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> emailValidationRule.validate(blankEmail));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.EMAIL_BLANK.getMessage());
    }

    @DisplayName("유효하지 않은 이메일 형식일 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenEmailIsInvalid() {
        // given
        String invalidEmail = "testexample";

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> emailValidationRule.validate(invalidEmail));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @DisplayName("유효한 이메일 형식일 경우 예외를 던지지 않는다.")
    @Test
    void validate_ShouldNotThrowException_WhenEmailIsValid() {
        // given
        String validEmail = "test@example.com";

        // when & then
        assertDoesNotThrow(() -> emailValidationRule.validate(validEmail));
    }
}