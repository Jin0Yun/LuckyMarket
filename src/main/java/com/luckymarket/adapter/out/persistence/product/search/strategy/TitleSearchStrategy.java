package com.luckymarket.adapter.out.persistence.product.search.strategy;

import com.luckymarket.adapter.out.persistence.product.search.ProductSearchStrategy;
import com.luckymarket.domain.entity.product.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSearchStrategy implements ProductSearchStrategy<String> {
    @Override
    public Specification<Product> apply(String title) {
        return (root, query, cb) ->
                (title == null || title.trim().isEmpty())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }
}
