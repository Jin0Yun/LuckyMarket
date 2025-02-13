package com.luckymarket.domain.exception.persistence;

public class ParticipationException extends RuntimeException {
    private final ParticipationErrorCode errorCode;

    public ParticipationException(ParticipationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
