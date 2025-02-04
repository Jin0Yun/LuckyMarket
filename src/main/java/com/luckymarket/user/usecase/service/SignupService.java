package com.luckymarket.user.usecase.service;

import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.usecase.dto.SignupRequestDto;

public interface SignupService {
    Member signup(SignupRequestDto signupRequestDto);
}