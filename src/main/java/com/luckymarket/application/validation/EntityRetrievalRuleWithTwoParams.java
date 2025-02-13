package com.luckymarket.application.validation;

public interface EntityRetrievalRuleWithTwoParams<T, ID1, ID2> extends ValidationRule<T> {
    T getEntity(ID1 id1, ID2 id2);
}
