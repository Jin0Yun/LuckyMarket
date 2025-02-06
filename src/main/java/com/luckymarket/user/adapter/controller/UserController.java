package com.luckymarket.user.adapter.controller;

import com.luckymarket.auth.security.SecurityContextService;
import com.luckymarket.common.ApiResponseWrapper;
import com.luckymarket.user.usecase.dto.*;
import com.luckymarket.user.usecase.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 정보 수정, 관리 API")
public class UserController {
    private final UserService userService;
    private final SecurityContextService securityContextService;

    @GetMapping("/get-user")
    @Operation(summary = "회원 정보 조회", description = "회원의 이름, 전화번호, 주소 등을 조회합니다.")
    public ApiResponseWrapper<MemberResponseDto> getUser() {
        Long userId = securityContextService.getCurrentUserId();
        MemberResponseDto member = userService.getUser(userId);
        return ApiResponseWrapper.success("회원 정보를 성공적으로 조회했습니다", member);
    }

    @PatchMapping("/update-password")
    @Operation(summary = "비밀번호 변경", description = "회원의 비밀번호를 변경합니다.")
    public ApiResponseWrapper<Void> changePassword(@RequestBody PasswordUpdateDto dto) {
        Long userId = securityContextService.getCurrentUserId();
        userService.changePassword(userId, dto);
        return ApiResponseWrapper.success("비밀번호가 성공적으로 변경되었습니다", null);
    }

    @PatchMapping("/update-phone")
    @Operation(summary = "전화번호 변경", description = "회원의 전화번호를 변경합니다.")
    public ApiResponseWrapper<MemberResponseDto> updatePhoneNumber(@RequestBody PhoneNumberUpdateDto dto) {
        Long userId = securityContextService.getCurrentUserId();
        MemberResponseDto member = userService.updatePhoneNumber(userId, dto);
        return ApiResponseWrapper.success("전화번호가 성공적으로 변경되었습니다", member);
    }

    @PatchMapping("/update-address")
    @Operation(summary = "주소 변경", description = "회원의 주소를 변경합니다.")
    public ApiResponseWrapper<MemberResponseDto> updateAddress(@RequestBody AddressUpdateDto dto) {
        Long userId = securityContextService.getCurrentUserId();
        MemberResponseDto member = userService.updateAddress(userId, dto);
        return ApiResponseWrapper.success("주소가 성공적으로 변경되었습니다", member);
    }

    @PatchMapping("/update-phone-address")
    @Operation(summary = "전화번호 및 주소 변경", description = "회원의 전화번호와 주소를 변경합니다.")
    public ApiResponseWrapper<MemberResponseDto> updatePhoneNumberAndAddress(@RequestBody PhoneNumberAndAddressUpdateDto phoneAndAddressDto) {
        Long userId = securityContextService.getCurrentUserId();
        MemberResponseDto member = userService.updatePhoneNumberAndAddress(userId, phoneAndAddressDto);
        return ApiResponseWrapper.success("전화번호와 주소가 성공적으로 업데이트되었습니다", member);
    }

    @PatchMapping("/update-name")
    @Operation(summary = "이름 변경", description = "회원의 이름을 변경합니다.")
    public ApiResponseWrapper<MemberResponseDto> updateName(@RequestBody NameUpdateDto nameUpdateDto) {
        Long userId = securityContextService.getCurrentUserId();
        MemberResponseDto member = userService.updateName(userId, nameUpdateDto);
        return ApiResponseWrapper.success("이름이 성공적으로 변경되었습니다", member);
    }

    @DeleteMapping("/delete-account")
    @Operation(summary = "회원 탈퇴", description = "회원 계정을 삭제합니다.")
    public ApiResponseWrapper<Void> deleteAccount() {
        Long userId = securityContextService.getCurrentUserId();
        userService.deleteAccount(userId);
        return ApiResponseWrapper.success("회원 탈퇴가 성공적으로 처리되었습니다", null);
    }
}