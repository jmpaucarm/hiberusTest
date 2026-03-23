package com.hiberius.paymentinitiation.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentOrderIdTest {

    @Test
    void ofTrimsValue() {
        assertThat(PaymentOrderId.of("  PO-1  ").value()).isEqualTo("PO-1");
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> PaymentOrderId.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("blank");
    }

    @Test
    void rejectsBlankCompactConstructor() {
        assertThatThrownBy(() -> new PaymentOrderId("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsIdExceedingMaxLength() {
        String tooLong = "P".repeat(129);
        assertThatThrownBy(() -> PaymentOrderId.of(tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximum length");
    }
}
