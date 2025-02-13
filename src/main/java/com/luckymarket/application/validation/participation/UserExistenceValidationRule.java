package com.luckymarket.application.validation.participation;

import com.luckymarket.adapter.out.persistence.user.UserRepository;
import com.luckymarket.application.validation.EntityRetrievalRule;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserExistenceValidationRule implements EntityRetrievalRule<Member, Long> {
    private final UserRepository userRepository;

    @Override
    public void validate(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new AuthException(AuthErrorCode.USER_NOT_FOUND);
        }
    }

    @Override
    public Member getEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
    }
}
