package com.luckymarket.application.service.participation.Impl;

import com.luckymarket.adapter.out.persistence.participation.ParticipationRepository;
import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.application.dto.participation.ParticipationRequest;
import com.luckymarket.application.service.participation.ParticipationService;
import com.luckymarket.application.validation.participation.UserExistenceValidationRule;
import com.luckymarket.application.validation.participation.ParticipationValidationRule;
import com.luckymarket.application.validation.participation.ParticipationExistenceValidationRule;
import com.luckymarket.application.validation.participation.ProductExistenceValidationRule;
import com.luckymarket.domain.entity.participation.Participation;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.entity.user.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;
    private final ProductRepository productRepository;
    private final ProductExistenceValidationRule productExistenceValidationRule;
    private final UserExistenceValidationRule userExistenceValidationRule;
    private final ParticipationValidationRule participationValidationRule;
    private final ParticipationExistenceValidationRule participationExistenceValidationRule;

    @Transactional
    @Override
    public void participateInProduct(Long productId, Long userId) {
        validateProductAndUser(productId, userId);

        Product product = productExistenceValidationRule.getEntity(productId);
        Member member = userExistenceValidationRule.getEntity(userId);

        ParticipationRequest request = new ParticipationRequest(product, userId);
        participationValidationRule.validate(request);

        Participation participation = Participation.builder()
                .product(product)
                .member(member)
                .build();

        participationRepository.save(participation);
        product.setParticipants(product.getParticipants() + 1);
        productRepository.save(product);
    }

    @Transactional
    @Override
    public void leaveProduct(Long productId, Long userId) {
        validateProductAndUser(productId, userId);

        Product product = productExistenceValidationRule.getEntity(productId);
        ParticipationRequest request = new ParticipationRequest(product, userId);
        Participation participation = participationExistenceValidationRule.getEntity(productId, userId);

        participationRepository.delete(participation);
        product.setParticipants(product.getParticipants() - 1);
        productRepository.save(product);
    }

    private void validateProductAndUser(Long productId, Long userId) {
        productExistenceValidationRule.validate(productId);
        userExistenceValidationRule.validate(userId);
    }
}