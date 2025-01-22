package com.luckymarket.user.validator;

import com.luckymarket.user.dto.AddressUpdateDto;
import com.luckymarket.user.dto.PhoneNumberAndAddressUpdateDto;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;

public class AddressValidator {
    public static void validate(AddressUpdateDto addressDto) {
        if (addressDto == null || addressDto.getAddress() == null || addressDto.getAddress().trim().isEmpty()) {
            throw new UserException(UserErrorCode.ADDRESS_BLANK);
        }
    }

    public static void validate(PhoneNumberAndAddressUpdateDto dto) {
        if (dto == null || dto.getAddress() == null || dto.getAddress().trim().isEmpty()) {
            throw new UserException(UserErrorCode.ADDRESS_BLANK);
        }
    }
}
