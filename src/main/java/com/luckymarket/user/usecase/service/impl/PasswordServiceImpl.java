package com.luckymarket.user.usecase.service.impl;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.user.usecase.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public void matches(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword))  {
            throw new AuthException(AuthErrorCode.PASSWORD_MISMATCH);
        }
    }
}
