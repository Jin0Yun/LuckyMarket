package com.luckymarket.application.validation.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordValidationRuleTest {

    private final PasswordValidationRule passwordValidationRule = new PasswordValidationRule();

    @DisplayName("비밀번호가 null이거나 빈 문자열일 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenPasswordIsBlank() {
        // given
        String blankPassword = "";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> passwordValidationRule.validate(blankPassword));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PASSWORD_BLANK.getMessage());
    }

    @DisplayName("비밀번호가 8자 미만일 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenPasswordIsTooShort() {
        // given
        String shortPassword = "short";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> passwordValidationRule.validate(shortPassword));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PASSWORD_TOO_SHORT.getMessage());
    }

    @DisplayName("비밀번호에 대문자가 없을 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenPasswordMissingUppercase() {
        // given
        String passwordWithoutUppercase = "password123!";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> passwordValidationRule.validate(passwordWithoutUppercase));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PASSWORD_MISSING_UPPERCASE.getMessage());
    }

    @DisplayName("비밀번호에 소문자가 없을 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenPasswordMissingLowercase() {
        // given
        String passwordWithoutLowercase = "PASSWORD123!";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> passwordValidationRule.validate(passwordWithoutLowercase));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PASSWORD_MISSING_LOWERCASE.getMessage());
    }

    @DisplayName("비밀번호에 특수 문자가 없을 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenPasswordMissingSpecialChar() {
        // given
        String passwordWithoutSpecialChar = "Password123";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> passwordValidationRule.validate(passwordWithoutSpecialChar));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PASSWORD_MISSING_SPECIAL_CHAR.getMessage());
    }

    @DisplayName("유효한 비밀번호일 경우 예외를 던지지 않는다.")
    @Test
    void validate_ShouldNotThrowException_WhenPasswordIsValid() {
        // given
        String validPassword = "ValidPassword123!";

        // when & then
        assertDoesNotThrow(() -> passwordValidationRule.validate(validPassword));
    }
}
