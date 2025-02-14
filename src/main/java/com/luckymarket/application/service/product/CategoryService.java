package com.luckymarket.application.service.product;

import com.luckymarket.domain.entity.product.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();
    List<Category> getParentCategories();
    List<Category> getSubCategories(Long parentId);
    Category getCategoryByCode(String code);
}
