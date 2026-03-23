package com.hiberius.paymentinitiation.domain.model;

import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void ofCreatesMoneyWithValidAmountAndCurrency() {
        Money m = Money.of(new BigDecimal("100.00"), "USD");

        assertThat(m.amount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(m.currencyCode()).isEqualTo("USD");
    }

    @Test
    void rejectsNullAmount() {
        assertThatThrownBy(() -> Money.of(null, "USD"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("amount is required");
    }

    @Test
    void rejectsAmountBelowMinimum() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("0.00"), "USD"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("0.01");
    }

    @Test
    void rejectsInvalidCurrencyFormat() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("1.00"), "usd"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("ISO 4217");
    }

    @Test
    void rejectsNullCurrency() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("1.00"), null))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
