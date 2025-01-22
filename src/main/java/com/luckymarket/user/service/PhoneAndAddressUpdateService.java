package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.AddressUpdateDto;
import com.luckymarket.user.dto.PhoneNumberUpdateDto;
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

    public Member updatePhoneNumber(Long userId, PhoneNumberUpdateDto phoneDto) {
        PhoneNumberValidator.validate(phoneDto);

        Member member = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        member.setPhoneNumber(phoneDto.getPhoneNumber());
        return userRepository.save(member);
    }

    public Member updateAddress(Long userId, AddressUpdateDto addressDto) {
        AddressValidator.validate(addressDto);

        Member member = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        member.setAddress(addressDto.getAddress());
        return userRepository.save(member);
    }
}
