package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.*;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.repository.UserRepository;
import com.luckymarket.user.service.account.AccountDeactivationService;
import com.luckymarket.user.service.password.PasswordChangeService;
import com.luckymarket.user.service.phoneAndAddress.PhoneAndAddressUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PhoneAndAddressUpdateService phoneAndAddressUpdateService;
    private final PasswordChangeService passwordChangeService;
    private final AccountDeactivationService accountDeactivationService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PhoneAndAddressUpdateService phoneAndAddressUpdateService,
            PasswordChangeService passwordChangeService,
            AccountDeactivationService accountDeactivationService
    ) {
        this.userRepository = userRepository;
        this.phoneAndAddressUpdateService = phoneAndAddressUpdateService;
        this.passwordChangeService = passwordChangeService;
        this.accountDeactivationService = accountDeactivationService;
    }

    @Override
    public Member getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Member updateName(Long userId, NameUpdateDto nameDto) {
        Member member = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NAME_BLANK));
        member.setUsername(nameDto.getNewName());
        return userRepository.save(member);
    }

    @Override
    public Member updatePhoneNumber(Long userId, PhoneNumberUpdateDto phoneDto) {
        return phoneAndAddressUpdateService.updatePhoneNumber(userId, phoneDto);
    }

    @Override
    public Member updateAddress(Long userId, AddressUpdateDto addressDto) {
        return phoneAndAddressUpdateService.updateAddress(userId, addressDto);
    }

    @Override
    public Member updatePhoneNumberAndAddress(Long userId, PhoneNumberAndAddressUpdateDto dto) {
        return phoneAndAddressUpdateService.updatePhoneNumberAndAddress(userId, dto);
    }

    @Override
    public Member changePassword(Long userId, PasswordUpdateDto passwordDto) {
        return passwordChangeService.changePassword(userId, passwordDto);
    }

    @Override
    public void deleteAccount(Long userId) {
        accountDeactivationService.deleteAccount(userId);
    }
}
