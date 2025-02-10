package com.luckymarket.application.validation;

import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoginPasswordValidationRuleTest {
    private LoginPasswordValidationRule loginPasswordValidationRule;

    @BeforeEach
    void setUp() {
        loginPasswordValidationRule = new LoginPasswordValidationRule();
    }

    @DisplayName("비밀번호가 빈 문자열일 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenPasswordIsBlank() {
        // given
        String blankPassword = "";

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> loginPasswordValidationRule.validate(blankPassword));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.PASSWORD_BLANK.getMessage());
    }

    @DisplayName("비밀번호가 유효할 경우 예외를 던지지 않는다.")
    @Test
    void validate_ShouldNotThrowException_WhenPasswordIsValid() {
        // given
        String validPassword = "Password123!";

        // when & then
        assertDoesNotThrow(() -> loginPasswordValidationRule.validate(validPassword));
    }
}