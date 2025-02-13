package com.luckymarket.adapter.in.web.auth;

import com.luckymarket.application.dto.auth.AuthTokenResponse;
import com.luckymarket.application.dto.ApiResponse;
import com.luckymarket.application.dto.auth.AuthRefreshTokenResponse;
import com.luckymarket.application.dto.auth.AuthLoginRequest;
import com.luckymarket.application.service.auth.AuthService;
import com.luckymarket.application.dto.auth.SignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "사용자 로그인, 로그아웃, 토큰 갱신 관련 API")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "사용자가 이메일, 비밀번호, 이름으로 회원가입을 요청합니다.")
    public ApiResponse<Void> signup(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ApiResponse.success("회원가입이 성공적으로 완료되었습니다.", null);
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "사용자가 이메일과 비밀번호를 입력해서 로그인하고, JWT 토큰을 반환받습니다."
    )
    public ApiResponse<Object> login(@RequestBody AuthLoginRequest authLoginRequest) {
        AuthTokenResponse responseDto = authService.login(authLoginRequest);
        return ApiResponse.success("로그인이 성공적으로 완료되었습니다.", responseDto);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃 처리. 클라이언트가 전달한 JWT 토큰을 블랙리스트에 추가하고, 해당 사용자의 리프레시 토큰을 삭제합니다."
    )
    public ApiResponse<Object> logout(@RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
        return ApiResponse.success("로그아웃이 성공적으로 처리되었습니다.", null);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "액세스 토큰 재발급",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다. 리프레시 토큰이 유효한 경우 새로운 액세스 토큰과 리프레시 토큰을 반환합니다."
    )
    public ApiResponse<Object> reissueAccessToken(@RequestHeader("Authorization") String refreshToken) {
        AuthRefreshTokenResponse jwtTokenDto = authService.refreshAccessToken(refreshToken);
        return ApiResponse.success("엑세스 토큰이 성공적으로 갱신되었습니다.", jwtTokenDto);
    }
}
