package com.luckymarket.user.service.signup;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.SignupRequestDto;

public interface SignupService {
    Member signup(SignupRequestDto signupRequestDto);
}