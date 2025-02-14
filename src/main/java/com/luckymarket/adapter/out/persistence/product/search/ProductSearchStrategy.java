package com.luckymarket.adapter.out.persistence.product.search;

import com.luckymarket.domain.entity.product.Product;
import org.springframework.data.jpa.domain.Specification;

public interface ProductSearchStrategy<T> {
    Specification<Product> apply(T value);
}