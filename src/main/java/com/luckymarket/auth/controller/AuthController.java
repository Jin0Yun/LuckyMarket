package com.luckymarket.auth.controller;

import com.luckymarket.common.ApiResponseWrapper;
import com.luckymarket.security.JwtTokenProvider;
import com.luckymarket.user.domain.Member;
import com.luckymarket.auth.dto.JwtTokenDto;
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
@RequestMapping("/api/member")
@Tag(name = "회원 API", description = "회원 관련 API")
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "사용자가 이메일과 비밀번호를 입력해서 로그인하고, JWT 토큰을 반환받습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "401", description = "잘못된 로그인 정보(이메일/비밀번호 불일치)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<ApiResponseWrapper<Object>> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            log.debug("로그인 요청 처리: 이메일 = {}", loginRequestDto.getEmail());
            Member member = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            String token = jwtTokenProvider.createToken(member.getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken();

            JwtTokenDto jwtTokenDto = new JwtTokenDto();
            jwtTokenDto.setAccessToken("Bearer " + token);
            jwtTokenDto.setRefreshToken(refreshToken);

            log.info("로그인 성공: 이메일 = {}", loginRequestDto.getEmail());
            ApiResponseWrapper<Object> response = ApiResponseWrapper.withData("로그인 성공", jwtTokenDto);
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
}
