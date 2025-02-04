package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.*;

public interface UserService {
    Member getUserById(Long userId);
    MemberResponseDto getUser(Long userId);
    MemberResponseDto updateName(Long userId, NameUpdateDto nameDto);
    MemberResponseDto updatePhoneNumber(Long userId, PhoneNumberUpdateDto phoneDto);
    MemberResponseDto updateAddress(Long userId, AddressUpdateDto addressDto);
    MemberResponseDto updatePhoneNumberAndAddress(Long userId, PhoneNumberAndAddressUpdateDto dto);
    void changePassword(Long userId, PasswordUpdateDto passwordDto);
    void deleteAccount(Long userId);
}
