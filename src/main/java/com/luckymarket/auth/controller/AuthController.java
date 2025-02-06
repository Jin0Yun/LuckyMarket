package com.luckymarket.auth.controller;

import com.luckymarket.auth.dto.LoginResponseDto;
import com.luckymarket.common.ApiResponseWrapper;
import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.service.AuthService;
import com.luckymarket.user.usecase.dto.SignupRequestDto;
import com.luckymarket.auth.service.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "사용자 로그인, 로그아웃, 토큰 갱신 관련 API")
public class AuthController {
    private final SignupService signupService;
    private final AuthService authService;

    @Autowired
    public AuthController(SignupService signupService, AuthService authService) {
        this.signupService = signupService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자가 이메일, 비밀번호, 이름으로 회원가입을 요청합니다.")
    public ApiResponseWrapper<Void> signup(@RequestBody SignupRequestDto signupRequestDto) {
        signupService.signup(signupRequestDto);
        return ApiResponseWrapper.success("회원가입 성공", null);
    }


    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "사용자가 이메일과 비밀번호를 입력해서 로그인하고, JWT 토큰을 반환받습니다."
    )
    public ApiResponseWrapper<Object> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto responseDto = authService.login(loginRequestDto);
        return ApiResponseWrapper.success("로그인 성공", responseDto);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃 처리. 클라이언트가 전달한 JWT 토큰을 블랙리스트에 추가하고, 해당 사용자의 리프레시 토큰을 삭제합니다."
    )
    public ApiResponseWrapper<Object> logout(@RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
        return ApiResponseWrapper.success("로그아웃 성공", null);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "액세스 토큰 재발급",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다. 클라이언트가 전달한 액세스 토큰을 검증하고 유효한 경우 새로운 액세스 토큰을 반환합니다."
    )
    public ApiResponseWrapper<Object> reissueAccessToken(@RequestHeader("Authorization") String accessToken) {
        String refreshToken = accessToken.replace("Bearer ", "");
        TokenResponseDto jwtTokenDto = authService.refreshAccessToken(refreshToken);
        return ApiResponseWrapper.success("엑세스 토큰 갱신 성공", jwtTokenDto);
    }
}
