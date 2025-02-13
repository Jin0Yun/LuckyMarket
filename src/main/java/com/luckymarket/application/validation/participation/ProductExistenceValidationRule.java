package com.luckymarket.application.validation.participation;

import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.application.validation.EntityRetrievalRule;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.exception.product.ProductErrorCode;
import com.luckymarket.domain.exception.product.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductExistenceValidationRule implements EntityRetrievalRule<Product, Long> {
    private final ProductRepository productRepository;

    @Override
    public void validate(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    @Override
    public Product getEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }
}
