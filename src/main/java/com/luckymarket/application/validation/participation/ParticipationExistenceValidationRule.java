package com.luckymarket.application.validation.participation;

import com.luckymarket.adapter.out.persistence.participation.ParticipationRepository;
import com.luckymarket.application.validation.EntityRetrievalRuleWithTwoParams;
import com.luckymarket.domain.entity.participation.Participation;
import com.luckymarket.domain.exception.persistence.ParticipationErrorCode;
import com.luckymarket.domain.exception.persistence.ParticipationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipationExistenceValidationRule implements EntityRetrievalRuleWithTwoParams<Participation, Long, Long> {
    private final ParticipationRepository participationRepository;

    @Override
    public void validate(Participation request) {
        if (participationRepository.existsByProductIdAndMemberId(request.getProduct().getId(), request.getMember().getId())) {
            throw new ParticipationException(ParticipationErrorCode.ALREADY_PARTICIPATED);
        }
    }

    @Override
    public Participation getEntity(Long productId, Long userId) {
        return participationRepository.findByProductIdAndMemberId(productId, userId)
                .orElseThrow(() -> new ParticipationException(ParticipationErrorCode.NOT_PARTICIPATED));
    }
}
