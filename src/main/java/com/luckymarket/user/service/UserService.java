package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.AddressUpdateDto;
import com.luckymarket.user.dto.NameUpdateDto;
import com.luckymarket.user.dto.PhoneNumberUpdateDto;

public interface UserService {
    Member updateName(Long userId, NameUpdateDto nameDto);
    Member updatePhoneNumber(Long userId, PhoneNumberUpdateDto phoneDto);
    Member updateAddress(Long userId, AddressUpdateDto addressDto);
    Member updatePhoneNumberAndAddress(Long userId, PhoneNumberUpdateDto phoneDto, AddressUpdateDto addressDto);
    Member changePassword(Long userId, String newPassword);
}
