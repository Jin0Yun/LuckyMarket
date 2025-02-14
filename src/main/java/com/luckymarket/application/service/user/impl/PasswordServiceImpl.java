package com.luckymarket.application.service.user.impl;

import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.application.service.user.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {
    private final PasswordEncoder passwordEncoder;

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
