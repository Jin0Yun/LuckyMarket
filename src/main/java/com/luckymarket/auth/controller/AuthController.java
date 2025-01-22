package com.luckymarket.auth.controller;

import com.luckymarket.common.ApiResponseWrapper;
import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "사용자 로그인, 로그아웃, 토큰 갱신 관련 API")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "사용자가 이메일과 비밀번호를 입력해서 로그인하고, JWT 토큰을 반환받습니다."
    )
    public ResponseEntity<ApiResponseWrapper<Object>> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            log.debug("로그인 요청 처리: 이메일 = {}", loginRequestDto.getEmail());
            TokenResponseDto tokenResponseDto = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            log.info("로그인 성공: 이메일 = {}", loginRequestDto.getEmail());
            ApiResponseWrapper<Object> response = ApiResponseWrapper.success("로그인 성공", tokenResponseDto);
            return ResponseEntity.ok(response);
        } catch (AuthException e) {
            log.error("로그인 실패: {}", e.getMessage());
            ApiResponseWrapper<Object> response = ApiResponseWrapper.error(e.getMessage(), HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            log.error("로그인 중 오류 발생: {}", e.getMessage());
            ApiResponseWrapper<Object> errorResponse = ApiResponseWrapper.error("예기치 못한 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃 처리. 클라이언트가 전달한 JWT 토큰을 블랙리스트에 추가하고, 해당 사용자의 리프레시 토큰을 삭제합니다."
    )
    public ResponseEntity<ApiResponseWrapper<Object>> logout(@RequestHeader("Authorization") String accessToken) {
        try {
            authService.logout(accessToken);
            return ResponseEntity.ok(ApiResponseWrapper.success("로그아웃 성공", null));
        } catch (AuthException e) {
            ApiResponseWrapper<Object> response = ApiResponseWrapper.error(e.getMessage(), HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponseWrapper<Object> errorResponse = ApiResponseWrapper.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "액세스 토큰 재발급",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다. 클라이언트가 전달한 액세스 토큰을 검증하고 유효한 경우 새로운 액세스 토큰을 반환합니다."
    )
    public ResponseEntity<ApiResponseWrapper<Object>> reissueAccessToken(@RequestHeader("Authorization") String accessToken) {
        try {
            TokenResponseDto jwtTokenDto = authService.refreshAccessToken(accessToken.replace("Bearer ", ""));
            return ResponseEntity.ok(ApiResponseWrapper.success("엑세스 토큰 갱신 성공", jwtTokenDto));
        } catch (AuthException e) {
            log.error("엑세스 토큰 갱신 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponseWrapper.error(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        }
    }
}
