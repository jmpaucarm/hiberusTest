package com.hiberius.paymentinitiation.domain.model;

import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;

public record RemittanceInfo(String value) {

    public RemittanceInfo {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleViolationException("remittanceInformation must not be blank when provided");
        }
        if (value.length() > 140) {
            throw new BusinessRuleViolationException("remittanceInformation exceeds maximum length");
        }
    }

    public static RemittanceInfo of(String value) {
        return new RemittanceInfo(value.trim());
    }
}
