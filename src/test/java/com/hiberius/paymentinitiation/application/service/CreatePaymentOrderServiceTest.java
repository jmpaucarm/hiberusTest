package com.hiberius.paymentinitiation.application.service;

import com.hiberius.paymentinitiation.domain.model.ExternalReference;
import com.hiberius.paymentinitiation.domain.model.Iban;
import com.hiberius.paymentinitiation.domain.model.Money;
import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderInitiation;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderStatus;
import com.hiberius.paymentinitiation.port.out.PaymentOrderIdGenerator;
import com.hiberius.paymentinitiation.port.out.PaymentOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePaymentOrderServiceTest {

    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2025-10-15T12:00:00Z"), ZoneOffset.UTC);

    @Mock
    private PaymentOrderRepository repository;

    @Mock
    private PaymentOrderIdGenerator idGenerator;

    private CreatePaymentOrderService service;

    @BeforeEach
    void setUp() {
        service = new CreatePaymentOrderService(repository, idGenerator, CLOCK);
    }

    @Test
    void createGeneratesIdAppliesDomainRulesPersistsAndReturnsReceivedOrder() {
        when(idGenerator.next()).thenReturn(PaymentOrderId.of("PO-000001"));
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-1"),
                Iban.of("EC12DEBTOR"),
                Iban.of("EC98CREDITOR"),
                Money.of(new BigDecimal("10.00"), "USD"),
                Optional.empty(),
                LocalDate.of(2025, 10, 20));

        var saved = service.create(initiation);

        assertThat(saved.id().value()).isEqualTo("PO-000001");
        assertThat(saved.status()).isEqualTo(PaymentOrderStatus.RECEIVED);
        assertThat(saved.createdAt()).isEqualTo(Instant.parse("2025-10-15T12:00:00Z"));
        ArgumentCaptor<com.hiberius.paymentinitiation.domain.model.PaymentOrder> captor =
                ArgumentCaptor.forClass(com.hiberius.paymentinitiation.domain.model.PaymentOrder.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().id().value()).isEqualTo("PO-000001");
        assertThat(captor.getValue().status()).isEqualTo(PaymentOrderStatus.RECEIVED);
    }

    @Test
    void createDoesNotPersistWhenDomainRejectsInitiation() {
        when(idGenerator.next()).thenReturn(PaymentOrderId.of("PO-X"));
        var invalidInitiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-1"),
                Iban.of("SAME"),
                Iban.of("same"),
                Money.of(new BigDecimal("10.00"), "USD"),
                Optional.empty(),
                LocalDate.of(2025, 10, 20));

        assertThatThrownBy(() -> service.create(invalidInitiation))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("differ");

        verify(repository, never()).save(any());
    }
}
