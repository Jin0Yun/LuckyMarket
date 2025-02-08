package com.luckymarket.auth.validator;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.common.validator.ValidationRule;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.model.Status;
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
