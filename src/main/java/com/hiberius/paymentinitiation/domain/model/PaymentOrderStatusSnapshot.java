package com.hiberius.paymentinitiation.domain.model;

import java.time.Instant;

/**
 * Read-model for status inquiries: aligns with the aggregate's current {@link PaymentOrder#status()} and
 * {@link PaymentOrder#statusChangedAt()} (last business status change).
 */
public record PaymentOrderStatusSnapshot(
        PaymentOrderId id,
        PaymentOrderStatus status,
        Instant lastUpdated) {

    /**
     * Builds the snapshot from the persisted aggregate so GET status stays consistent with GET full order.
     */
    public static PaymentOrderStatusSnapshot from(PaymentOrder order) {
        return new PaymentOrderStatusSnapshot(order.id(), order.status(), order.statusChangedAt());
    }
}
