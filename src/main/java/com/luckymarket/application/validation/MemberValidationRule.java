package com.luckymarket.application.validation;

import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.domain.entity.user.Status;
import org.springframework.stereotype.Component;

@Component
public class MemberValidationRule implements ValidationRule<Member> {
    @Override
    public void validate(Member member) {
        if (member == null) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_FOUND);
        }
        if (member.getStatus() == Status.DELETED) {
            throw new AuthException(AuthErrorCode.USER_ALREADY_DELETED);
        }
    }
}
