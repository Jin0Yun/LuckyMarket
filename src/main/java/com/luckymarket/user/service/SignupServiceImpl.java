package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.exception.SignupErrorCode;
import com.luckymarket.user.exception.SignupException;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class SignupServiceImpl implements SignupService {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private final UserRepository userRepository;

    @Autowired
    public SignupServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void signup(SignupRequestDto signupRequestDto) {
        validateEmail(signupRequestDto.getEmail());
        validatePassword(signupRequestDto.getPassword());

        Member member = signupRequestDto.toEntity(signupRequestDto.getPassword());
        userRepository.save(member);
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new SignupException(SignupErrorCode.EMAIL_BLANK);
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new SignupException(SignupErrorCode.INVALID_EMAIL_FORMAT);
        }
        if (userRepository.existsByEmail(email)) {
            throw new SignupException(SignupErrorCode.EMAIL_ALREADY_USED);
        }
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