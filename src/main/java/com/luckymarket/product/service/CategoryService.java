package com.luckymarket.product.service;

import com.luckymarket.product.domain.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getCategories();
    List<Category> getParentCategories();
    List<Category> getSubCategories(Long parentId);
    Optional<Category> getCategoryByCode(String code);
}
