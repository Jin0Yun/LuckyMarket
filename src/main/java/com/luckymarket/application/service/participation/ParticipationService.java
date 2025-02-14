package com.luckymarket.application.service.participation;

public interface ParticipationService {
    void participateInProduct(Long productId, Long userId);
    void leaveProduct(Long productId, Long userId);
}