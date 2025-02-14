package com.luckymarket.adapter.out.persistence.product.search.strategy;

import com.luckymarket.adapter.out.persistence.product.search.ProductSearchStrategy;
import com.luckymarket.domain.entity.product.PriceRange;
import com.luckymarket.domain.entity.product.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceSearchStrategy implements ProductSearchStrategy<PriceRange> {
    @Override
    public Specification<Product> apply(PriceRange priceRange) {
        return (root, query, cb) -> {
            BigDecimal priceMin = priceRange.getPriceMin();
            BigDecimal priceMax = priceRange.getPriceMax();

            if (priceMin != null && priceMax != null) {
                return cb.between(root.get("price"), priceMin, priceMax);
            } else if (priceMin != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), priceMin);
            } else if (priceMax != null) {
                return cb.lessThanOrEqualTo(root.get("price"), priceMax);
            }
            return cb.conjunction();
        };
    }
}
