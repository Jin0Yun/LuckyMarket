package com.luckymarket.auth.service;

import com.luckymarket.user.usecase.dto.SignupRequestDto;

public interface SignupService {
    void signup(SignupRequestDto signupRequestDto);
}