package com.hiberius.paymentinitiation.domain.model;

import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IbanTest {

    @Test
    void ofTrimsWhitespace() {
        assertThat(Iban.of("  EC12  ").value()).isEqualTo("EC12");
    }

    @Test
    void rejectsBlankIban() {
        assertThatThrownBy(() -> Iban.of("   "))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("iban must not be blank");
    }

    @Test
    void rejectsIbanExceedingMaxLength() {
        String tooLong = "X".repeat(65);
        assertThatThrownBy(() -> Iban.of(tooLong))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("maximum length");
    }
}
