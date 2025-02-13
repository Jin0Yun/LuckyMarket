package com.luckymarket.application.validation.participation;

import com.luckymarket.adapter.out.persistence.participation.ParticipationRepository;
import com.luckymarket.application.dto.ParticipationRequest;
import com.luckymarket.application.validation.ValidationRule;
import com.luckymarket.domain.entity.participation.Participation;
import com.luckymarket.domain.exception.persistence.ParticipationErrorCode;
import com.luckymarket.domain.exception.persistence.ParticipationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipationExistenceValidationRule implements ValidationRule<ParticipationRequest> {
    private final ParticipationRepository participationRepository;

    @Override
    public void validate(ParticipationRequest request) {
        if (!participationRepository.existsByProductIdAndMemberId(request.getProduct().getId(), request.getUserId())) {
            throw new ParticipationException(ParticipationErrorCode.NOT_PARTICIPATED);
        }
    }

    public Participation getParticipation(Long productId, Long userId) {
        return participationRepository.findByProductIdAndMemberId(productId, userId)
                .orElseThrow(() -> new ParticipationException(ParticipationErrorCode.NOT_PARTICIPATED));
    }
}