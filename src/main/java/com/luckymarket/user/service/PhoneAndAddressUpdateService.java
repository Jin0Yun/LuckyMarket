package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneAndAddressUpdateService {
    private final UserRepository userRepository;

    @Autowired
    public PhoneAndAddressUpdateService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Member updatePhoneNumber(Long userId, String newPhoneNumber) {
        validatePhoneNumber(newPhoneNumber);

        Member member = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.PHONE_NUMBER_BLANK));

        member.setPhoneNumber(newPhoneNumber);
        return userRepository.save(member);
    }

    public Member updateAddress(Long userId, String newAddress) {
        return null;
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new UserException(UserErrorCode.PHONE_NUMBER_BLANK);
        }

        if (!phoneNumber.matches("^\\+?[0-9]{10,15}$")) {
            throw new UserException(UserErrorCode.INVALID_PHONE_NUMBER_FORMAT);
        }
    }

    private void validateAddress(String address) {

    }
}
