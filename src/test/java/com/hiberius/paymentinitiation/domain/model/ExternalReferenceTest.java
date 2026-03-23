package com.hiberius.paymentinitiation.domain.model;

import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExternalReferenceTest {

    @Test
    void ofTrimsAndAcceptsValidReference() {
        assertThat(ExternalReference.of("  REF-99  ").value()).isEqualTo("REF-99");
    }

    @Test
    void rejectsNullInFactory() {
        assertThatThrownBy(() -> ExternalReference.of(null))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("externalReference must not be blank");
    }

    @Test
    void rejectsBlankAfterTrimInCompactConstructor() {
        assertThatThrownBy(() -> new ExternalReference("   "))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void rejectsReferenceExceedingMaxLength() {
        String tooLong = "R".repeat(65);
        assertThatThrownBy(() -> ExternalReference.of(tooLong))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("maximum length");
    }
}
