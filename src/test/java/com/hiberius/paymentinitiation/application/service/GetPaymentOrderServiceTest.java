package com.hiberius.paymentinitiation.application.service;

import com.hiberius.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.hiberius.paymentinitiation.domain.model.ExternalReference;
import com.hiberius.paymentinitiation.domain.model.Iban;
import com.hiberius.paymentinitiation.domain.model.Money;
import com.hiberius.paymentinitiation.domain.model.PaymentOrder;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderInitiation;
import com.hiberius.paymentinitiation.port.out.PaymentOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPaymentOrderServiceTest {

    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2025-10-15T12:00:00Z"), ZoneOffset.UTC);

    @Mock
    private PaymentOrderRepository repository;

    private GetPaymentOrderService service;

    @BeforeEach
    void setUp() {
        service = new GetPaymentOrderService(repository);
    }

    @Test
    void getReturnsOrderWhenPresentInRepository() {
        PaymentOrderId id = PaymentOrderId.of("PO-FOUND");
        PaymentOrder expected = buildOrder(id);

        when(repository.findById(id)).thenReturn(Optional.of(expected));

        assertThat(service.get(id)).isSameAs(expected);
        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getThrowsPaymentOrderNotFoundWhenAbsent() {
        PaymentOrderId id = PaymentOrderId.of("PO-MISSING");
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(id))
                .isInstanceOf(PaymentOrderNotFoundException.class)
                .hasMessageContaining("PO-MISSING");

        verify(repository).findById(id);
    }

    private static PaymentOrder buildOrder(PaymentOrderId id) {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-G"),
                Iban.of("D"),
                Iban.of("C"),
                Money.of(new BigDecimal("5.00"), "USD"),
                Optional.empty(),
                LocalDate.of(2025, 11, 1));
        return PaymentOrder.initiate(id, initiation, CLOCK);
    }
}
