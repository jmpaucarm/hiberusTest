package com.hiberius.paymentinitiation.domain.model;

import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemittanceInfoTest {

    @Test
    void ofTrimsValue() {
        assertThat(RemittanceInfo.of("  note  ").value()).isEqualTo("note");
    }

    @Test
    void rejectsBlankCompactConstructor() {
        assertThatThrownBy(() -> new RemittanceInfo("  "))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("remittanceInformation");
    }

    @Test
    void rejectsTextExceedingMaxLength() {
        String tooLong = "x".repeat(141);
        assertThatThrownBy(() -> RemittanceInfo.of(tooLong))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("maximum length");
    }
}
