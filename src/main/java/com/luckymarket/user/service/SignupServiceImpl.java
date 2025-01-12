package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
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
            throw new IllegalArgumentException("비밀번호는 필수 항목입니다.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("비밀번호는 최소 1개의 대문자를 포함해야 합니다.");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("비밀번호는 최소 1개의 소문자를 포함해야 합니다.");
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new IllegalArgumentException("비밀번호는 최소 1개의 특수문자를 포함해야 합니다.");
        }
    }
}