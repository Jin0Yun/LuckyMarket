package com.luckymarket.application.service.user;

public interface MemberValidationService {
    void validateUser(Long userId);
    void validatePassword(String password);
    void validatePhoneNumber(String phoneNumber);
    void validateAddress(String address);
}
