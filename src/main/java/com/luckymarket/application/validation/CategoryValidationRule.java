package com.luckymarket.application.validation;

import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.exception.product.CategoryErrorCode;
import com.luckymarket.domain.exception.product.CategoryException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryValidationRule implements ValidationRule<List<Category> > {
    @Override
    public void validate(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }
    }

    public void validateParentCategoryExists(boolean parentExists) {
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
}
