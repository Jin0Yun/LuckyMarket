package com.luckymarket.adapter.in.web.participation;

import com.luckymarket.application.dto.ApiResponse;
import com.luckymarket.application.service.participation.ParticipationService;
import com.luckymarket.infrastructure.security.SecurityContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "공동구매 API", description = "공동구매 참여/참여해제? 관련 API")
public class ParticipationController {
    private final ParticipationService participationService;
    private final SecurityContextService securityContextService;

    @PostMapping("/{productId}/participations")
    @Operation(summary = "상품 참여", description = "유저가 공동구매에 참여합니다.")
    public ApiResponse<String> participateInProduct(@PathVariable Long productId) {
        Long userId = securityContextService.getCurrentUserId();
        participationService.participateInProduct(productId, userId);
        return ApiResponse.success("공동구매 참여가 완료되었습니다.", null);
    }

    @DeleteMapping("/{productId}/participations")
    @Operation(summary = "상품 참여 취소", description = "유저가 공동구매 참여를 취소합니다.")
    public ApiResponse<String> leaveProduct(@PathVariable Long productId) {
        Long userId = securityContextService.getCurrentUserId();
        participationService.leaveProduct(productId, userId);
        return ApiResponse.success("공동구매 참여가 취소되었습니다.", null);
    }
}
