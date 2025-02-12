package com.luckymarket.adapter.out.persistence.product.search.strategy;

import com.luckymarket.adapter.out.persistence.product.search.ProductSearchStrategy;
import com.luckymarket.domain.entity.product.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CategoryCodeSearchStrategy implements ProductSearchStrategy<String> {
    @Override
    public Specification<Product> apply(String categoryCode) {
        return (root, query, cb) ->
                (categoryCode == null || categoryCode.trim().isEmpty())
                        ? cb.conjunction()
                        : cb.equal(root.get("category").get("code"), categoryCode);
    }
}
