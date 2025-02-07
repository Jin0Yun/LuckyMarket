package com.luckymarket.auth.service;

import com.luckymarket.user.domain.model.Member;

import com.luckymarket.user.usecase.dto.SignupRequestDto;
import com.luckymarket.user.adapter.mapper.MemberMapper;
import com.luckymarket.user.domain.repository.UserRepository;
import com.luckymarket.user.usecase.service.MemberValidationService;
import com.luckymarket.user.usecase.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupServiceImpl implements SignupService {
    private final UserRepository userRepository;
    private final MemberValidationService memberValidationService;
    private final PasswordService passwordService;
    private final MemberMapper memberMapper;

    @Autowired
    public SignupServiceImpl(
            UserRepository userRepository,
            MemberValidationService memberValidator,
            PasswordService passwordService,
            MemberMapper memberMapper
    ) {
        this.userRepository = userRepository;
        this.memberValidationService = memberValidator;
        this.passwordService = passwordService;
        this.memberMapper = memberMapper;
    }

    @Override
    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        memberValidationService.validateSignupFields(signupRequestDto);
        signupRequestDto.setPassword(passwordService.encodePassword(signupRequestDto.getPassword()));
        Member member = memberMapper.toEntity(signupRequestDto);
        userRepository.save(member);
    }
}