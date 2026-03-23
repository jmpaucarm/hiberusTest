package com.hiberius.paymentinitiation.application.service;

import com.hiberius.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.hiberius.paymentinitiation.domain.model.ExternalReference;
import com.hiberius.paymentinitiation.domain.model.Iban;
import com.hiberius.paymentinitiation.domain.model.Money;
import com.hiberius.paymentinitiation.domain.model.PaymentOrder;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderInitiation;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderStatusSnapshot;
import com.hiberius.paymentinitiation.port.in.GetPaymentOrderUseCase;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPaymentOrderStatusServiceTest {

    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2025-10-15T12:00:00Z"), ZoneOffset.UTC);

    @Mock
    private GetPaymentOrderUseCase getPaymentOrderUseCase;

    private GetPaymentOrderStatusService service;

    @BeforeEach
    void setUp() {
        service = new GetPaymentOrderStatusService(getPaymentOrderUseCase);
    }

    @Test
    void loadReturnsStatusSnapshotWhenOrderExists() {
        PaymentOrderId id = PaymentOrderId.of("PO-STATUS-1");
        PaymentOrder order = buildOrder(id);

        when(getPaymentOrderUseCase.get(id)).thenReturn(order);

        PaymentOrderStatusSnapshot snapshot = service.load(id);

        assertThat(snapshot.id()).isEqualTo(id);
        assertThat(snapshot.status()).isEqualTo(order.status());
        assertThat(snapshot.lastUpdated()).isEqualTo(order.statusChangedAt());
        verify(getPaymentOrderUseCase).get(id);
    }

    @Test
    void loadThrowsNotFoundWhenUseCaseDoesNotFindOrder() {
        PaymentOrderId id = PaymentOrderId.of("PO-GONE");

        when(getPaymentOrderUseCase.get(id)).thenThrow(new PaymentOrderNotFoundException(id));

        assertThatThrownBy(() -> service.load(id))
                .isInstanceOf(PaymentOrderNotFoundException.class)
                .hasMessageContaining("PO-GONE");
    }

    private static PaymentOrder buildOrder(PaymentOrderId id) {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-S"),
                Iban.of("D1"),
                Iban.of("C1"),
                Money.of(new BigDecimal("20.00"), "USD"),
                Optional.empty(),
                LocalDate.of(2025, 11, 15));
        return PaymentOrder.initiate(id, initiation, CLOCK);
    }
}
