package com.hiberius.paymentinitiation.domain.model;


public record PaymentOrderId(String value) {

    public PaymentOrderId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("paymentOrderId must not be blank");
        }
        if (value.length() > 128) {
            throw new IllegalArgumentException("paymentOrderId exceeds maximum length");
        }
    }

    /**
     * Builds an id from a path or string parameter (leading/trailing spaces are trimmed).
     */
    public static PaymentOrderId of(String value) {
        if (value == null) {
            throw new IllegalArgumentException("paymentOrderId must not be blank");
        }
        return new PaymentOrderId(value.trim());
    }
}
