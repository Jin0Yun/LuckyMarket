package com.luckymarket.user.usecase.service;

import com.luckymarket.user.usecase.dto.SignupRequestDto;

public interface MemberValidationService {
    void validateUser(Long userId);
    void validateSignupFields(SignupRequestDto dto);
    void validateEmail(String email);
    void validatePassword(String password);
    void validatePhoneNumber(String phoneNumber);
    void validateAddress(String address);
}
