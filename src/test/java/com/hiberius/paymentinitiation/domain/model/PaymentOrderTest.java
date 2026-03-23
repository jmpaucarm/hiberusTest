package com.hiberius.paymentinitiation.domain.model;

import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentOrderTest {

    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2025-10-15T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void initiateCreatesReceivedOrder() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-1"),
                Iban.of("EC12DEBTOR"),
                Iban.of("EC98CREDITOR"),
                Money.of(new BigDecimal("150.75"), "USD"),
                Optional.of(RemittanceInfo.of("Factura 1")),
                LocalDate.of(2025, 10, 31));

        PaymentOrder order = PaymentOrder.initiate(PaymentOrderId.of("PO-000001"), initiation, CLOCK);

        assertThat(order.id().value()).isEqualTo("PO-000001");
        assertThat(order.status()).isEqualTo(PaymentOrderStatus.RECEIVED);
        assertThat(order.externalReference().value()).isEqualTo("EXT-1");
        assertThat(order.debtorIban().value()).isEqualTo("EC12DEBTOR");
        assertThat(order.creditorIban().value()).isEqualTo("EC98CREDITOR");
        assertThat(order.money().currencyCode()).isEqualTo("USD");
        assertThat(order.money().amount()).isEqualByComparingTo(new BigDecimal("150.75"));
        assertThat(order.remittance()).hasValue(RemittanceInfo.of("Factura 1"));
        assertThat(order.requestedExecutionDate()).isEqualTo(LocalDate.of(2025, 10, 31));
        assertThat(order.createdAt()).isEqualTo(order.statusChangedAt());
        assertThat(order.createdAt()).isEqualTo(Instant.parse("2025-10-15T12:00:00Z"));
    }

    @Test
    void initiateAcceptsExecutionDateEqualToToday() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-TODAY"),
                Iban.of("A"),
                Iban.of("B"),
                Money.of(new BigDecimal("10.00"), "USD"),
                Optional.empty(),
                LocalDate.of(2025, 10, 15));

        PaymentOrder order = PaymentOrder.initiate(PaymentOrderId.of("PO-TODAY"), initiation, CLOCK);

        assertThat(order.requestedExecutionDate()).isEqualTo(LocalDate.of(2025, 10, 15));
        assertThat(order.status()).isEqualTo(PaymentOrderStatus.RECEIVED);
    }

    @Test
    void initiateWithoutRemittanceLeavesRemittanceEmpty() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-2"),
                Iban.of("X"),
                Iban.of("Y"),
                Money.of(new BigDecimal("1.00"), "EUR"),
                Optional.empty(),
                LocalDate.of(2025, 12, 1));

        PaymentOrder order = PaymentOrder.initiate(PaymentOrderId.of("PO-2"), initiation, CLOCK);

        assertThat(order.remittance()).isEmpty();
    }

    @Test
    void replaceStatusUpdatesStatusAndLastChangeInstant() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-1"),
                Iban.of("A"),
                Iban.of("B"),
                Money.of(new BigDecimal("10.00"), "USD"),
                Optional.empty(),
                LocalDate.of(2025, 11, 1));

        PaymentOrder order = PaymentOrder.initiate(PaymentOrderId.of("PO-1"), initiation, CLOCK);
        Instant created = order.createdAt();

        Clock later = Clock.offset(CLOCK, Duration.ofHours(3));
        order.replaceStatus(PaymentOrderStatus.PENDING, later);

        assertThat(order.status()).isEqualTo(PaymentOrderStatus.PENDING);
        assertThat(order.statusChangedAt()).isEqualTo(Instant.parse("2025-10-15T15:00:00Z"));
        assertThat(order.createdAt()).isEqualTo(created);
    }

    @Test
    void initiateRejectsEqualDebtorAndCreditor() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-1"),
                Iban.of("EC12DEBTOR"),
                Iban.of("EC12DEBTOR"),
                Money.of(new BigDecimal("10.00"), "USD"),
                Optional.empty(),
                LocalDate.of(2025, 10, 31));

        assertThatThrownBy(() -> PaymentOrder.initiate(PaymentOrderId.of("PO-1"), initiation, CLOCK))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("differ");
    }

    @Test
    void initiateRejectsSameIbanIgnoringCase() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-1"),
                Iban.of("EC12DEBTOR"),
                Iban.of("ec12debtor"),
                Money.of(new BigDecimal("10.00"), "USD"),
                Optional.empty(),
                LocalDate.of(2025, 10, 31));

        assertThatThrownBy(() -> PaymentOrder.initiate(PaymentOrderId.of("PO-1"), initiation, CLOCK))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("differ");
    }

    @Test
    void initiateRejectsPastExecutionDate() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-1"),
                Iban.of("A"),
                Iban.of("B"),
                Money.of(new BigDecimal("10.00"), "USD"),
                Optional.empty(),
                LocalDate.of(2025, 10, 14));

        assertThatThrownBy(() -> PaymentOrder.initiate(PaymentOrderId.of("PO-1"), initiation, CLOCK))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("past");
    }
}
