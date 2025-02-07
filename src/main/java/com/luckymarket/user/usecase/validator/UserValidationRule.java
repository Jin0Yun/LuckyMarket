package com.luckymarket.user.usecase.validator;

import com.luckymarket.auth.security.SecurityContextService;
import com.luckymarket.common.validator.ValidationRule;
import com.luckymarket.user.domain.exception.UserErrorCode;
import com.luckymarket.user.domain.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserValidationRule implements ValidationRule<Long> {
    private final SecurityContextService securityContextService;

    @Autowired
    public UserValidationRule(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }

    @Override
    public void validate(Long userId) {
        Long currentUserId = securityContextService.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new UserException(UserErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}