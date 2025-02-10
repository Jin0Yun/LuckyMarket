package com.luckymarket.application.service.product.impl;

import com.luckymarket.adapter.out.persistence.product.CategoryRepository;
import com.luckymarket.application.service.product.CategoryService;
import com.luckymarket.application.validation.CategoryValidationRule;
import com.luckymarket.domain.entity.product.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryValidationRule categoryValidationRule;

    @Override
    public List<Category> getCategories() {
        List<Category> categories = categoryRepository.findAll();
        categoryValidationRule.validate(categories);
        return categories;
    }

    @Override
    public List<Category> getParentCategories() {
        List<Category> categories = categoryRepository.findByParentIsNull();
        categoryValidationRule.validate(categories);
        return categories;
    }

    @Override
    public List<Category> getSubCategories(Long parentId) {
        boolean parentExists = categoryRepository.existsById(parentId);
        categoryValidationRule.validateParentCategoryExists(parentExists);
        List<Category> subCategories = categoryRepository.findByParent(parentId);
        categoryValidationRule.validateSubCategoriesExist(subCategories);
        return subCategories;
    }

    @Override
    public Category getCategoryByCode(String code) {
        Category category = categoryRepository.findByCode(code);
        categoryValidationRule.validateCategoryCodeExists(category);
        return category;
    }
}
