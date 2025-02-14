package com.luckymarket.application.service.user;

public interface PasswordService {
    String encodePassword(String password);
    void matches(String password, String encodedPassword);
}
