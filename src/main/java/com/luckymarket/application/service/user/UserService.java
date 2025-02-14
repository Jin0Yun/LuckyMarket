package com.luckymarket.application.service.user;

import com.luckymarket.application.dto.user.*;
import com.luckymarket.domain.entity.user.Member;

import java.util.List;

public interface UserService {
    Member getUserById(Long userId);
    UserProfileResponse getUser(Long userId);
    UserProfileResponse updateName(Long userId, UserNameUpdateRequest dto);
    UserProfileResponse updatePhoneNumber(Long userId, UserPhoneUpdateRequest dto);
    UserProfileResponse updateAddress(Long userId, UserAddressUpdateRequest addressDto);
    UserProfileResponse updatePhoneNumberAndAddress(Long userId, UserContactUpdateRequest dto);
    void changePassword(Long userId, UserPasswordUpdateRequest dto);
    void deleteAccount(Long userId);
    List<UserProductSummaryResponse> getCreatedProducts(Long userId);
    List<UserParticipatedProductResponse> getParticipatedProducts(Long userId);
}
