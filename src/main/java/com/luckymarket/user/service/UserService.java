package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.*;

public interface UserService {
    Member getUserById(Long userId);
    Member updateName(Long userId, NameUpdateDto nameDto);
    Member updatePhoneNumber(Long userId, PhoneNumberUpdateDto phoneDto);
    Member updateAddress(Long userId, AddressUpdateDto addressDto);
    Member updatePhoneNumberAndAddress(Long userId, PhoneNumberAndAddressUpdateDto dto);
    Member changePassword(Long userId, PasswordUpdateDto passwordDto);
    void deleteAccount(Long userId);
}
