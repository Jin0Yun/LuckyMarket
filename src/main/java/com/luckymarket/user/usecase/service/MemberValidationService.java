package com.luckymarket.user.usecase.service;

public interface MemberValidationService {
    void validateUser(Long userId);
    void validatePassword(String password);
    void validatePhoneNumber(String phoneNumber);
    void validateAddress(String address);
}
