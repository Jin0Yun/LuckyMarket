package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.PhoneNumberUpdateDto;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneNumberUpdateService {
    private final UserRepository userRepository;

    @Autowired
    public PhoneNumberUpdateService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Member updatePhoneNumber(Long userId, PhoneNumberUpdateDto phoneDto) {
        return null;
    }

    public Member updatePhoneNumber(Long userId, String newPhoneNumber) {
        return null;
    }

    private void validatePhoneNumber(String phoneNumber) {
    }
}
