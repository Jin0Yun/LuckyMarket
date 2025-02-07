package com.luckymarket.user.usecase.service;

public interface PasswordService {
    String encodePassword(String password);
    void matches(String password, String encodedPassword);
}
