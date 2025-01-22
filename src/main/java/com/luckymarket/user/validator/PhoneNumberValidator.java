package com.luckymarket.user.validator;

import com.luckymarket.user.dto.PhoneNumberAndAddressUpdateDto;
import com.luckymarket.user.dto.PhoneNumberUpdateDto;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;

public class PhoneNumberValidator {

    public static void validate(PhoneNumberUpdateDto phoneDto) {
        String phoneNumber = phoneDto.getPhoneNumber();

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new UserException(UserErrorCode.PHONE_NUMBER_BLANK);
        }
        if (!phoneNumber.matches("^\\+?[0-9]{10,15}$")) {
            throw new UserException(UserErrorCode.INVALID_PHONE_NUMBER_FORMAT);
        }
    }

    public static void validate(PhoneNumberAndAddressUpdateDto dto) {
        String phoneNumber = dto.getPhoneNumber();

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new UserException(UserErrorCode.PHONE_NUMBER_BLANK);
        }
        if (!phoneNumber.matches("^\\+?[0-9]{10,15}$")) {
            throw new UserException(UserErrorCode.INVALID_PHONE_NUMBER_FORMAT);
        }
    }
}
