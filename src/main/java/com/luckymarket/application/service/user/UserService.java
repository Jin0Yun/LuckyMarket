package com.luckymarket.application.service.user;

import com.luckymarket.application.dto.user.*;
import com.luckymarket.domain.entity.user.Member;

public interface UserService {
    Member getUserById(Long userId);
    MemberResponseDto getUser(Long userId);
    MemberResponseDto updateName(Long userId, NameUpdateDto dto);
    MemberResponseDto updatePhoneNumber(Long userId, PhoneNumberUpdateDto dto);
    MemberResponseDto updateAddress(Long userId, AddressUpdateDto addressDto);
    MemberResponseDto updatePhoneNumberAndAddress(Long userId, PhoneNumberAndAddressUpdateDto dto);
    void changePassword(Long userId, PasswordUpdateDto dto);
    void deleteAccount(Long userId);
}
