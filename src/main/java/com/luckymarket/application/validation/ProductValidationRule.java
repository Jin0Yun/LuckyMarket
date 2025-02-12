package com.luckymarket.application.validation;

import com.luckymarket.application.dto.product.ProductCreateDto;
import com.luckymarket.domain.exception.product.ProductErrorCode;
import com.luckymarket.domain.exception.product.ProductException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class ProductValidationRule implements ValidationRule<ProductCreateDto> {
    @Override
    public void validate(ProductCreateDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new ProductException(ProductErrorCode.TITLE_BLANK);
        }
        if (dto.getDescription() == null || dto.getDescription().isEmpty()) {
            throw new ProductException(ProductErrorCode.DESCRIPTION_BLANK);
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductException(ProductErrorCode.INVALID_PRICE);
        }
        if (dto.getCategoryCode() == null || dto.getCategoryCode().isEmpty()) {
            throw new ProductException(ProductErrorCode.CATEGORY_BLANK);
        }
        if (dto.getMaxParticipants() <= 0) {
            throw new ProductException(ProductErrorCode.MAX_PARTICIPANTS_INVALID);
        }
        if (dto.getEndDate() == null || dto.getEndDate().isBefore(LocalDate.now())) {
            throw new ProductException(ProductErrorCode.DATE_INVALID);
        }
    }
}
