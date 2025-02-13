package com.luckymarket.application.dto.user;

import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.entity.product.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "유저가 참여한 상품 목록 반환 DTO")
public class UserParticipatedProductResponse {
    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "상품 제목", example = "신선한 사과")
    private String productTitle;

    @Schema(description = "상품 가격", example = "19900")
    private BigDecimal productPrice;

    @Schema(description = "상품 카테고리")
    private Category productCategory;

    @Schema(description = "참여자 수", example = "5")
    private int participants;

    @Schema(description = "최대 참여자 수", example = "20")
    private int maxParticipants;

    @Schema(description = "상품 상태 (예: 판매중, 종료 등)")
    private ProductStatus productStatus;

    @Schema(description = "상품 이미지 URL", example = "https://example.com/image.jpg")
    private String productImageUrl;

    @Schema(description = "상품 종료 날짜", example = "2025-12-31")
    private LocalDate productEndDate;
}
