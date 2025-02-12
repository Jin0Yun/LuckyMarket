package com.luckymarket.application.validation.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.luckymarket.infrastructure.security.SecurityContextService;
import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserValidationRuleTest {
    @Mock
    private SecurityContextService securityContextService;

    @InjectMocks
    private UserValidationRule userValidationRule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("현재 사용자 ID와 입력된 ID가 다르면 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenUserIdDoesNotMatchCurrentUserId() {
        // given
        Long currentUserId = 1L;
        Long userId = 2L;
        when(securityContextService.getCurrentUserId()).thenReturn(currentUserId);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userValidationRule.validate(userId));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.UNAUTHORIZED_ACCESS.getMessage());
    }

    @DisplayName("현재 사용자 ID와 입력된 ID가 동일하면 예외가 발생하지 않는다.")
    @Test
    void validate_ShouldNotThrowException_WhenUserIdMatchesCurrentUserId() {
        // given
        Long currentUserId = 1L;
        Long userId = 1L;
        when(securityContextService.getCurrentUserId()).thenReturn(currentUserId);

        // when & then
        assertDoesNotThrow(() -> userValidationRule.validate(userId));
    }
}
