package com.hiberius.paymentinitiation.domain.model;

import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public record Money(BigDecimal amount, String currencyCode) {

    private static final Pattern ISO_4217 = Pattern.compile("^[A-Z]{3}$");

    public Money {
        if (amount == null) {
            throw new BusinessRuleViolationException("amount is required");
        }
        if (amount.compareTo(new BigDecimal("0.01")) < 0) {
            throw new BusinessRuleViolationException("amount must be at least 0.01");
        }
        if (currencyCode == null || !ISO_4217.matcher(currencyCode).matches()) {
            throw new BusinessRuleViolationException("currency must be a 3-letter ISO 4217 code");
        }
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, currencyCode);
    }
}
