package com.luckymarket.user.controller;

import com.luckymarket.common.ApiResponseWrapper;
import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.service.signup.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "회원 가입 API", description = "사용자 회원 가입 관련 API")
public class SignupController {
    private final SignupService signupService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자가 이메일, 비밀번호, 이름으로 회원가입을 요청합니다.")
    public ApiResponseWrapper<String> signup(@RequestBody SignupRequestDto signupRequestDto) {
        signupService.signup(signupRequestDto);
        return ApiResponseWrapper.success("회원가입 성공", null);
    }
}