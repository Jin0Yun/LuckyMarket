package com.luckymarket.application.dto.product;

import com.luckymarket.domain.entity.product.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "새 상품 등록 요청 DTO")
public class ProductCreateRequest {
    @Schema(description = "상품 제목", example = "신선한 사과")
    private String title;

    @Schema(description = "상품 설명", example = "100% 유기농 사과, 맛있고 건강한 사과입니다.")
    private String description;

    @Schema(description = "상품 가격", example = "5000", required = true)
    private BigDecimal price;

    @Schema(description = "상품 카테고리 코드", example = "A000", required = true)
    private String categoryCode;

    @Schema(description = "상품 상태 (ONGOING, CLOSED, COMPLETED)", required = true)
    private ProductStatus status;

    @Schema(description = "최대 참여 인원", example = "100", required = true)
    private int maxParticipants;

    @Schema(description = "상품 모집 종료 날짜", example = "2025-03-10", required = true)
    private LocalDate endDate;

    @Schema(description = "상품 이미지 URL", example = "image-url", required = false)
    private String imageUrl;
}
