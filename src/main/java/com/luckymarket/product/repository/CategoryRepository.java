package com.luckymarket.product.repository;

import com.luckymarket.product.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParent(Long parentId);
    List<Category> findByParentIsNull();
    Category findByCode(String code);
}
