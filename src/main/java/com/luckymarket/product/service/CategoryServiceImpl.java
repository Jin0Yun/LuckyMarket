package com.luckymarket.product.service;

import com.luckymarket.product.domain.Category;
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
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getParentCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Override
    public List<Category> getSubCategories(Long parentId) {
        return categoryRepository.findByParent(parentId);
    }

    @Override
    public Optional<Category> getCategoryByCode(String code) {
        return categoryRepository.findByCode(code);
    }
}
