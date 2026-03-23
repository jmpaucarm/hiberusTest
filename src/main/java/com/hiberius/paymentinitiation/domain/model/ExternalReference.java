package com.hiberius.paymentinitiation.domain.model;

import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;

public record ExternalReference(String value) {

    public ExternalReference {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleViolationException("externalReference must not be blank");
        }
        if (value.length() > 64) {
            throw new BusinessRuleViolationException("externalReference exceeds maximum length");
        }
    }

    public static ExternalReference of(String value) {
        if (value == null) {
            throw new BusinessRuleViolationException("externalReference must not be blank");
        }
        return new ExternalReference(value.trim());
    }
}
