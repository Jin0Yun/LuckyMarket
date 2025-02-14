package com.luckymarket.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "상품에 참여한 사용자 정보 DTO")
public class ProductParticipantInfo {
    @Schema(description = "참여자 ID", example = "2")
    private Long participantId;

    @Schema(description = "참여자 사용자 이름", example = "김철수")
    private String username;

}