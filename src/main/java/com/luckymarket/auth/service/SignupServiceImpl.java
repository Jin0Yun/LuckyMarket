package com.luckymarket.auth.service;

import com.luckymarket.auth.SignupMapper;
import com.luckymarket.auth.validator.AuthValidationService;
import com.luckymarket.user.domain.exception.UserErrorCode;
import com.luckymarket.user.domain.exception.UserException;
import com.luckymarket.user.domain.model.Member;

import com.luckymarket.auth.dto.SignupRequestDto;
import com.luckymarket.user.domain.repository.UserRepository;
import com.luckymarket.user.usecase.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupServiceImpl implements SignupService {
    private final UserRepository userRepository;
    private final AuthValidationService authValidationService;
    private final PasswordService passwordService;
    private final SignupMapper signupMapper;

    @Autowired
    public SignupServiceImpl(
            UserRepository userRepository,
            AuthValidationService authValidationService,
            PasswordService passwordService,
            SignupMapper signupMapper
    ) {
        this.userRepository = userRepository;
        this.authValidationService = authValidationService;
        this.passwordService = passwordService;
        this.signupMapper = signupMapper;
    }

    @Override
    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        authValidationService.validateEmail(signupRequestDto.getEmail());
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_USED);
        }

        authValidationService.validatePassword(signupRequestDto.getPassword());

        signupRequestDto.setPassword(passwordService.encodePassword(signupRequestDto.getPassword()));
        Member member = signupMapper.toEntity(signupRequestDto);
        userRepository.save(member);
    }
}