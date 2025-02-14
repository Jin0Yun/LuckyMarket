package com.luckymarket.application.validation;

public interface EntityRetrievalRule<T, ID> extends ValidationRule<ID> {
    T getEntity(ID id);
}