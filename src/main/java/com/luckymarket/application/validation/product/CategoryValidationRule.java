package com.luckymarket.application.validation.product;

import com.luckymarket.adapter.out.persistence.product.CategoryRepository;
import com.luckymarket.application.validation.ValidationRule;
import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.exception.product.CategoryErrorCode;
import com.luckymarket.domain.exception.product.CategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryValidationRule implements ValidationRule<List<Category>> {
    private final CategoryRepository categoryRepository;

    @Override
    public void validate(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    public void validateParentCategoryExists(Long parentCategoryId) {
        boolean parentExists = categoryRepository.existsById(parentCategoryId);
        if (!parentExists) {
            throw new CategoryException(CategoryErrorCode.PARENT_CATEGORY_NOT_FOUND);
        }
    }

    public void validateSubCategoriesExist(List<Category> subCategories) {
        if (subCategories == null || subCategories.isEmpty()) {
            throw new CategoryException(CategoryErrorCode.NO_SUBCATEGORY_FOUND);
        }
    }

    public void validateCategoryCodeExists(Category category) {
        if (category == null) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_CODE_NOT_FOUND);
        }
    }

    public Category getCategory(String categoryCode) {
        return categoryRepository.findByCode(categoryCode)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
    }
}
