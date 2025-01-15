package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.exception.LoginErrorCode;
import com.luckymarket.user.exception.LoginException;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Member login(String email, String password) {
        validateEmail(email);
        validatePassword(password);

        Member member = userRepository.findByEmail(email);
        if (member == null) {
            throw new LoginException(LoginErrorCode.EMAIL_NOT_FOUND);
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new LoginException(LoginErrorCode.PASSWORD_MISMATCH);
        }
        return member;
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new LoginException(LoginErrorCode.EMAIL_BLANK);
        }

        if (!isValidEmailFormat(email)) {
            throw new LoginException(LoginErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new LoginException(LoginErrorCode.PASSWORD_BLANK);
        }
    }

    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }
}
