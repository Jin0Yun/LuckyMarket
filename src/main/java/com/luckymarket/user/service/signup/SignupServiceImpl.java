package com.luckymarket.user.service.signup;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.mapper.MemberMapper;
import com.luckymarket.user.repository.UserRepository;
import com.luckymarket.user.service.MemberValidationService;
import com.luckymarket.user.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignupServiceImpl implements SignupService {
    private final UserRepository userRepository;
    private final MemberValidationService memberValidationService;
    private final PasswordService passwordService;

    @Autowired
    public SignupServiceImpl(
            UserRepository userRepository,
            MemberValidationService memberValidator,
            PasswordService passwordService
    ) {
        this.userRepository = userRepository;
        this.memberValidationService = memberValidator;
        this.passwordService = passwordService;
    }

    @Override
    public Member signup(SignupRequestDto signupRequestDto) {
        Member member = MemberMapper.toEntity(signupRequestDto);
        memberValidationService.validateSignupFields(member);

        String encodedPassword = passwordService.encodePassword(member.getPassword());
        member.setPassword(encodedPassword);

        userRepository.save(member);
        return member;
    }
}