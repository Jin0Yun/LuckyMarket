package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.AddressUpdateDto;
import com.luckymarket.user.dto.NameUpdateDto;
import com.luckymarket.user.dto.PhoneNumberUpdateDto;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PhoneAndAddressUpdateService phoneAndAddressUpdateService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PhoneAndAddressUpdateService phoneAndAddressUpdateService) {
        this.userRepository = userRepository;
        this.phoneAndAddressUpdateService = phoneAndAddressUpdateService;
    }

    @Override
    public Member updateName(Long userId, NameUpdateDto nameDto) {
        Member member = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NAME_BLANK));
        member.setUsername(nameDto.getNewName());
        return userRepository.save(member);
    }

    @Override
    public Member updatePhoneNumber(Long userId, PhoneNumberUpdateDto phoneDto) {
        return phoneAndAddressUpdateService.updatePhoneNumber(userId, phoneDto);
    }

    @Override
    public Member updateAddress(Long userId, AddressUpdateDto addressDto) {
        return phoneAndAddressUpdateService.updateAddress(userId, addressDto);
    }

    @Override
    public Member updatePhoneNumberAndAddress(Long userId, PhoneNumberUpdateDto phoneDto, AddressUpdateDto addressDto) {
        return null;
    }
}
