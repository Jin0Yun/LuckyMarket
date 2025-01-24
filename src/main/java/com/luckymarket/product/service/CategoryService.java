package com.luckymarket.product.service;

import com.luckymarket.product.domain.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();
    List<Category> getParentCategories();
    List<Category> getSubCategories(Long parentId);
    Category getCategoryByCode(String code);
}
