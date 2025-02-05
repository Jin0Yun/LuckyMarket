package com.luckymarket.user.usecase.service;

import com.luckymarket.user.usecase.dto.*;
import com.luckymarket.user.domain.model.Member;

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
