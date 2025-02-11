package com.luckymarket.application.validation;

import com.luckymarket.infrastructure.security.SecurityContextService;
import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidationRule implements ValidationRule<Long> {
    private final SecurityContextService securityContextService;

    @Override
    public void validate(Long userId) {
        Long currentUserId = securityContextService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new UserException(UserErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}