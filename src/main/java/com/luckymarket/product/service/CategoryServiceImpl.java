package com.luckymarket.product.service;

import com.luckymarket.product.domain.Category;
import com.luckymarket.product.exception.CategoryErrorCode;
import com.luckymarket.product.exception.CategoryException;
import com.luckymarket.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories == null || categories.isEmpty()) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND);
        }
        return categories;
    }

    @Override
    public List<Category> getParentCategories() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        if (categories == null || categories.isEmpty()) {
            throw new CategoryException(CategoryErrorCode.PARENT_CATEGORY_NOT_FOUND);
        }
        return categories;
    }

    @Override
    public List<Category> getSubCategories(Long parentId) {
        boolean parentExists = categoryRepository.existsById(parentId);
        if (!parentExists) {
            throw new CategoryException(CategoryErrorCode.PARENT_CATEGORY_NOT_FOUND);
        }
        List<Category> subCategories = categoryRepository.findByParent(parentId);
        if (subCategories == null || subCategories.isEmpty()) {
            throw new CategoryException(CategoryErrorCode.NO_SUBCATEGORY_FOUND);
        }

        return subCategories;
    }

    @Override
    public Category getCategoryByCode(String code) {
        Category category = Optional.ofNullable(categoryRepository.findByCode(code))
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_CODE_NOT_FOUND));

        return category;
    }
}
