package com.luckymarket.domain.exception.persistence;

public enum ParticipationErrorCode {
    PRODUCT_CLOSED("해당 상품은 마감되었습니다."),
    PRODUCT_FULL("최대 참여 인원이 찼습니다."),
    ALREADY_PARTICIPATED("이미 참여한 상품입니다."),
    INVALID_PARTICIPATION("참여할 수 없는 상품입니다."),
    CANNOT_PARTICIPATE_OWN_PRODUCT("본인이 올린 상품에는 참여할 수 없습니다."),
    NOT_PARTICIPATED("참여하지 않은 상품입니다.");

    private final String message;

    ParticipationErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
