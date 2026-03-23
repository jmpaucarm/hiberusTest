package com.hiberius.paymentinitiation.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentOrderStatusSnapshotTest {

    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2025-10-15T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void fromMapsIdStatusAndLastUpdatedFromAggregate() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-1"),
                Iban.of("DEBTOR"),
                Iban.of("CREDITOR"),
                Money.of(new BigDecimal("10.00"), "USD"),
                Optional.empty(),
                LocalDate.of(2025, 11, 1));

        PaymentOrder order = PaymentOrder.initiate(PaymentOrderId.of("PO-99"), initiation, CLOCK);
        PaymentOrderStatusSnapshot snapshot = PaymentOrderStatusSnapshot.from(order);

        assertThat(snapshot.id()).isEqualTo(order.id());
        assertThat(snapshot.status()).isEqualTo(PaymentOrderStatus.RECEIVED);
        assertThat(snapshot.lastUpdated()).isEqualTo(order.statusChangedAt());
    }
}
