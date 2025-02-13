package com.luckymarket.application.validation.participation;

import com.luckymarket.adapter.out.persistence.participation.ParticipationRepository;
import com.luckymarket.application.dto.participation.ParticipationRequest;
import com.luckymarket.application.validation.ValidationRule;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.entity.product.ProductStatus;
import com.luckymarket.domain.exception.persistence.ParticipationErrorCode;
import com.luckymarket.domain.exception.persistence.ParticipationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParticipationValidationRule implements ValidationRule<ParticipationRequest> {
    private final ParticipationRepository participationRepository;

    @Override
    public void validate(ParticipationRequest request) {
        Product product = request.getProduct();
        Long userId = request.getUserId();

        if (product.getStatus() == ProductStatus.CLOSED) {
            throw new ParticipationException(ParticipationErrorCode.PRODUCT_CLOSED);
        }
        if (product.getParticipants() >= product.getMaxParticipants()) {
            throw new ParticipationException(ParticipationErrorCode.PRODUCT_FULL);
        }
        if (participationRepository.existsByProductIdAndMemberId(product.getId(), userId)) {
            throw new ParticipationException(ParticipationErrorCode.ALREADY_PARTICIPATED);
        }
        if (product.getMember().getId().equals(userId)) {
            throw new ParticipationException(ParticipationErrorCode.CANNOT_PARTICIPATE_OWN_PRODUCT);
        }
    }
}
