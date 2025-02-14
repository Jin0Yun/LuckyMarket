package com.luckymarket.application.dto.participation;

import com.luckymarket.domain.entity.product.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "참여 요청 DTO")
public class ParticipationRequest {
    @Schema(description = "참여한 상품", required = true)
    private final Product product;

    @Schema(description = "참여한 사용자 ID", required = true)
    private final Long userId;
}
