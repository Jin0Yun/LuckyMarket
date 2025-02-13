package com.luckymarket.domain.entity.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PriceRange {
    private final BigDecimal priceMin;
    private final BigDecimal priceMax;
}
