package com.luckymarket.user.controller;

import com.luckymarket.common.ApiResponseWrapper;
import com.luckymarket.security.JwtTokenProvider;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.JwtTokenDto;
import com.luckymarket.user.dto.LoginRequestDto;
import com.luckymarket.user.exception.LoginException;
import com.luckymarket.user.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@Tag(name = "회원 API", description = "회원 관련 API")
public class LoginController {
    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public LoginController(LoginService loginService, JwtTokenProvider jwtTokenProvider) {
        this.loginService = loginService;
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
            Member member = loginService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            String token = jwtTokenProvider.createToken(member.getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken();

            JwtTokenDto jwtTokenDto = new JwtTokenDto();
            jwtTokenDto.setAccessToken("Bearer " + token);
            jwtTokenDto.setRefreshToken(refreshToken);

            ApiResponseWrapper<Object> response = ApiResponseWrapper.withData("로그인 성공", jwtTokenDto);
            return ResponseEntity.ok(response);
        } catch (LoginException e) {
            ApiResponseWrapper<Object> response = ApiResponseWrapper.error(e.getMessage(), HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponseWrapper<Object> errorResponse = ApiResponseWrapper.error("예기치 못한 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
