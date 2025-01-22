package com.luckymarket.user.service;

import com.luckymarket.user.dto.AddressUpdateDto;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;

public class AddressValidator {

    public static void validate(AddressUpdateDto addressDto) {
        if (addressDto == null || addressDto.getAddress().trim().isEmpty()) {
            throw new UserException(UserErrorCode.ADDRESS_BLANK);
        }
    }
}
