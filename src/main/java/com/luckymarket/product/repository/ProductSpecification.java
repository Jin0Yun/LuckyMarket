package com.luckymarket.product.repository;

import com.luckymarket.product.domain.Product;
import com.luckymarket.product.domain.ProductStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {
    public static Specification<Product> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("title"), "%" + title + "%");
        };
    }

    public static Specification<Product> hasCategoryCode(String categoryCode) {
        return (root, query, criteriaBuilder) -> {
            if (categoryCode == null || categoryCode.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("code"), categoryCode);
        };
    }

    public static Specification<Product> hasPriceBetween(BigDecimal priceMin, BigDecimal priceMax) {
        return (root, query, criteriaBuilder) -> {
            Predicate pricePredicate = criteriaBuilder.conjunction();

            if (priceMin != null) {
                pricePredicate = criteriaBuilder.and(pricePredicate, criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceMin));
            }

            if (priceMax != null) {
                pricePredicate = criteriaBuilder.and(pricePredicate, criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceMax));
            }

            return pricePredicate;
        };
    }

    public static Specification<Product> hasStatus(ProductStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
