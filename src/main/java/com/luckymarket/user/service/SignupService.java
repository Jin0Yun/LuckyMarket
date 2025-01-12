package com.luckymarket.user.service;

import com.luckymarket.user.dto.SignupRequestDto;

public interface SignupService {
    void signup(SignupRequestDto signupRequestDto);
}