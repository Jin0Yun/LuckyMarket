package com.luckymarket.application.dto.product;

import com.luckymarket.domain.entity.product.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "상품 검색 조건 DTO")
public class ProductSearchRequest {
    @Schema(description = "상품 제목", example = "가방")
    private String title;

    @Schema(description = "카테고리 코드", example = "C001")
    private String categoryCode;

    @Schema(description = "최소 가격", example = "1000")
    private BigDecimal priceMin;

    @Schema(description = "최대 가격", example = "5000")
    private BigDecimal priceMax;

    @Schema(description = "상품 상태", example = "ONGOING")
    private ProductStatus status;
}
