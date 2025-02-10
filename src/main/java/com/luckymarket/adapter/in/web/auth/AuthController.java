package com.luckymarket.adapter.in.web.auth;

import com.luckymarket.application.dto.auth.LoginResponseDto;
import com.luckymarket.adapter.in.web.ApiResponse;
import com.luckymarket.application.dto.auth.TokenResponseDto;
import com.luckymarket.application.dto.auth.LoginRequestDto;
import com.luckymarket.application.service.auth.AuthService;
import com.luckymarket.application.dto.auth.SignupRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "사용자 로그인, 로그아웃, 토큰 갱신 관련 API")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자가 이메일, 비밀번호, 이름으로 회원가입을 요청합니다.")
    public ApiResponse<Void> signup(@RequestBody SignupRequestDto signupRequestDto) {
        authService.signup(signupRequestDto);
        return ApiResponse.success("회원가입 성공", null);
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "사용자가 이메일과 비밀번호를 입력해서 로그인하고, JWT 토큰을 반환받습니다."
    )
    public ApiResponse<Object> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto responseDto = authService.login(loginRequestDto);
        return ApiResponse.success("로그인 성공", responseDto);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃 처리. 클라이언트가 전달한 JWT 토큰을 블랙리스트에 추가하고, 해당 사용자의 리프레시 토큰을 삭제합니다."
    )
    public ApiResponse<Object> logout(@RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
        return ApiResponse.success("로그아웃 성공", null);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "액세스 토큰 재발급",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다. 리프레시 토큰이 유효한 경우 새로운 액세스 토큰과 리프레시 토큰을 반환합니다."
    )
    public ApiResponse<Object> reissueAccessToken(@RequestHeader("Authorization") String refreshToken) {
        TokenResponseDto jwtTokenDto = authService.refreshAccessToken(refreshToken);
        return ApiResponse.success("엑세스 토큰 갱신 성공", jwtTokenDto);
    }
}
