package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.SignupRequestDto;

public interface SignupService {
    void signup(SignupRequestDto signupRequestDto);
}