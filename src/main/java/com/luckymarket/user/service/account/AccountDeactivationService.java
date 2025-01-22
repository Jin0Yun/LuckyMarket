package com.luckymarket.user.service.account;

import com.luckymarket.auth.service.redis.RedisService;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.domain.Status;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountDeactivationService {
    private final UserRepository userRepository;
    private final RedisService redisService;

    @Autowired
    public AccountDeactivationService(UserRepository userRepository, RedisService redisService) {
        this.userRepository = userRepository;
        this.redisService = redisService;
    }

    public void deleteAccount(Long userId) {
        Member member = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        if (member.getStatus() == Status.DELETED) {
            throw new UserException(UserErrorCode.USER_ALREADY_DELETED);
        }

        redisService.deleteRefreshToken(userId);
        member.setStatus(Status.DELETED);
        userRepository.save(member);
    }
}
