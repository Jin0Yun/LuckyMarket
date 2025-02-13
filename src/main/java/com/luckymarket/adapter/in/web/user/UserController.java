package com.luckymarket.adapter.in.web.user;

import com.luckymarket.application.dto.user.*;
import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import com.luckymarket.infrastructure.security.SecurityContextService;
import com.luckymarket.application.dto.ApiResponse;
import com.luckymarket.application.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 정보 수정, 관리 API")
public class UserController {
    private final UserService userService;
    private final SecurityContextService securityContextService;

    @GetMapping("/get-user")
    @Operation(summary = "회원 정보 조회", description = "회원의 이름, 전화번호, 주소 등을 조회합니다.")
    public ApiResponse<UserProfileResponse> getUser() {
        Long userId = securityContextService.getCurrentUserId();
        UserProfileResponse member = userService.getUser(userId);
        return ApiResponse.success("회원 정보를 성공적으로 조회했습니다", member);
    }

    @PatchMapping("/update-password")
    @Operation(summary = "비밀번호 변경", description = "회원의 비밀번호를 변경합니다.")
    public ApiResponse<Void> changePassword(@RequestBody UserPasswordUpdateRequest dto) {
        Long userId = securityContextService.getCurrentUserId();
        userService.changePassword(userId, dto);
        return ApiResponse.success("비밀번호가 성공적으로 변경되었습니다", null);
    }

    @PatchMapping("/update-phone")
    @Operation(summary = "전화번호 변경", description = "회원의 전화번호를 변경합니다.")
    public ApiResponse<UserProfileResponse> updatePhoneNumber(@RequestBody UserPhoneUpdateRequest dto) {
        Long userId = securityContextService.getCurrentUserId();
        UserProfileResponse member = userService.updatePhoneNumber(userId, dto);
        return ApiResponse.success("전화번호가 성공적으로 변경되었습니다", member);
    }

    @PatchMapping("/update-address")
    @Operation(summary = "주소 변경", description = "회원의 주소를 변경합니다.")
    public ApiResponse<UserProfileResponse> updateAddress(@RequestBody UserAddressUpdateRequest dto) {
        Long userId = securityContextService.getCurrentUserId();
        UserProfileResponse member = userService.updateAddress(userId, dto);
        return ApiResponse.success("주소가 성공적으로 변경되었습니다", member);
    }

    @PatchMapping("/update-phone-address")
    @Operation(summary = "전화번호 및 주소 변경", description = "회원의 전화번호와 주소를 변경합니다.")
    public ApiResponse<UserProfileResponse> updatePhoneNumberAndAddress(@RequestBody UserContactUpdateRequest phoneAndAddressDto) {
        Long userId = securityContextService.getCurrentUserId();
        UserProfileResponse member = userService.updatePhoneNumberAndAddress(userId, phoneAndAddressDto);
        return ApiResponse.success("전화번호와 주소가 성공적으로 업데이트되었습니다", member);
    }

    @PatchMapping("/update-name")
    @Operation(summary = "이름 변경", description = "회원의 이름을 변경합니다.")
    public ApiResponse<UserProfileResponse> updateName(@RequestBody UserNameUpdateRequest userNameUpdateRequest) {
        Long userId = securityContextService.getCurrentUserId();
        UserProfileResponse member = userService.updateName(userId, userNameUpdateRequest);
        return ApiResponse.success("이름이 성공적으로 변경되었습니다", member);
    }

    @DeleteMapping("/delete-account")
    @Operation(summary = "회원 탈퇴", description = "회원 계정을 삭제합니다.")
    public ApiResponse<Void> deleteAccount() {
        Long userId = securityContextService.getCurrentUserId();
        userService.deleteAccount(userId);
        return ApiResponse.success("회원 탈퇴가 성공적으로 처리되었습니다", null);
    }

    @GetMapping("/get-created-products")
    @Operation(
            summary = "회원이 생성한 상품 조회",
            description = "회원이 생성한 상품 목록을 조회합니다. 해당 회원이 생성한 모든 상품을 반환합니다."
    )
    public ApiResponse<List<UserProductSummaryResponse>> getCreatedProducts() {
        Long userId = securityContextService.getCurrentUserId();
        List<UserProductSummaryResponse> createdProducts = userService.getCreatedProducts(userId);
        return ApiResponse.success("성공적으로 생성한 상품 목록을 조회했습니다.", createdProducts);
    }

    @GetMapping("/get-participated")
    @Operation(
            summary = "회원이 참여한 상품 조회",
            description = "회원이 참여한 상품 목록을 조회합니다. 해당 회원이 참여한 모든 상품을 반환합니다."
    )
    public ApiResponse<List<UserParticipatedProductResponse>> getParticipatedProducts() {
        Long userId = securityContextService.getCurrentUserId();
        List<UserParticipatedProductResponse> participatedProducts = userService.getParticipatedProducts(userId);

        if (participatedProducts.isEmpty()) {
            throw new UserException(UserErrorCode.NO_PARTICIPATED_PRODUCTS_FOUND);
        }

        return ApiResponse.success("성공적으로 참여한 상품 목록을 조회했습니다.", participatedProducts);
    }
}