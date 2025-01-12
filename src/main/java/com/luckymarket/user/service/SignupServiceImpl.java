package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.exception.SignupErrorCode;
import com.luckymarket.user.exception.SignupException;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignupServiceImpl implements SignupService {
    private final UserRepository userRepository;

    @Autowired
    public SignupServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void signup(Member member) {
        validatePassword(member.getPassword());

        userRepository.save(member);
    }

    private static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new SignupException(SignupErrorCode.PASSWORD_BLANK);
        }

        if (password.length() < 8) {
            throw new SignupException(SignupErrorCode.PASSWORD_TOO_SHORT);
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new SignupException(SignupErrorCode.PASSWORD_MISSING_UPPERCASE);
        }

        if (!password.matches(".*[a-z].*")) {
            throw new SignupException(SignupErrorCode.PASSWORD_MISSING_LOWERCASE);
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new SignupException(SignupErrorCode.PASSWORD_MISSING_SPECIAL_CHAR);
        }
    }
}