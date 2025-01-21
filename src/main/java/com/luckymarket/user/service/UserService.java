package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.NameUpdateDto;

public interface UserService {
    Member updateName(Long userId, NameUpdateDto nameDto);
    Member updatePhoneNumber(Long userId, String newPhoneNumber);
    Member updateAddress(Long userId, String newAddress);
}
