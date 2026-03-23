package com.hiberius.paymentinitiation.domain.model;

import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;

public record Iban(String value) {

    public Iban {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleViolationException("iban must not be blank");
        }
        if (value.length() > 64) {
            throw new BusinessRuleViolationException("iban exceeds maximum length");
        }
    }

    public static Iban of(String value) {
        return new Iban(value.trim());
    }
}
