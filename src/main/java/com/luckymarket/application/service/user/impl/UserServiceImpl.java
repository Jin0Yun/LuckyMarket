package com.luckymarket.application.service.user.impl;

import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.application.dto.user.*;
import com.luckymarket.application.validation.participation.UserExistenceValidationRule;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.mapper.ProductMapper;
import com.luckymarket.infrastructure.redis.RedisService;
import com.luckymarket.domain.mapper.UserMapper;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.domain.entity.user.Status;
import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import com.luckymarket.application.service.user.MemberValidationService;
import com.luckymarket.application.service.user.PasswordService;
import com.luckymarket.application.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final ProductRepository productRepository;
    private final RedisService redisService;
    private final PasswordService passwordService;
    private final MemberValidationService memberValidationService;
    private final UserExistenceValidationRule userExistenceValidationRule;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Member getUserById(Long userId) {
        memberValidationService.validateUser(userId);
        return userExistenceValidationRule.getEntity(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUser(Long userId) {
        Member member = getUserById(userId);
        return userMapper.toMemberResponseDto(member);
    }

    @Override
    @Transactional
    public UserProfileResponse updateName(Long userId, UserNameUpdateRequest dto) {
        Member member = getUserById(userId);
        member.setUsername(dto.getNewName());
        return userMapper.toMemberResponseDto(member);
    }

    @Override
    @Transactional
    public UserProfileResponse updatePhoneNumber(Long userId, UserPhoneUpdateRequest dto) {
        Member member = getUserById(userId);
        memberValidationService.validatePhoneNumber(dto.getPhoneNumber());
        member.setPhoneNumber(dto.getPhoneNumber());
        return userMapper.toMemberResponseDto(member);
    }

    @Override
    @Transactional
    public UserProfileResponse updateAddress(Long userId, UserAddressUpdateRequest dto) {
        Member member = getUserById(userId);
        memberValidationService.validateAddress(dto.getAddress());
        member.setAddress(dto.getAddress());
        return userMapper.toMemberResponseDto(member);
    }

    @Override
    @Transactional
    public UserProfileResponse updatePhoneNumberAndAddress(Long userId, UserContactUpdateRequest dto) {
        Member member = getUserById(userId);
        memberValidationService.validateAddress(dto.getAddress());
        memberValidationService.validatePhoneNumber(dto.getPhoneNumber());
        member.setPhoneNumber(dto.getPhoneNumber());
        member.setAddress(dto.getAddress());
        return userMapper.toMemberResponseDto(member);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, UserPasswordUpdateRequest dto) {
        Member member = getUserById(userId);
        memberValidationService.validatePassword(dto.getPassword());
        String encodedPassword = passwordService.encodePassword(dto.getPassword());
        member.setPassword(encodedPassword);
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
        Member member = getUserById(userId);
        if (member.getStatus() == Status.DELETED) {
            throw new UserException(UserErrorCode.USER_ALREADY_DELETED);
        }
        redisService.removeRefreshToken(userId);
        redisService.markUserAsLoggedOut(userId);
        member.setStatus(Status.DELETED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProductSummaryResponse> getCreatedProducts(Long userId) {
        List<Product> createdProducts = productRepository.findByMemberId(userId);
        if (createdProducts.isEmpty()) {
            throw new UserException(UserErrorCode.NO_CREATED_PRODUCTS_FOUND);
        }
        return createdProducts.stream()
                .map(productMapper::toProductSummaryResponse)
                .collect(Collectors.toList());
    }
}