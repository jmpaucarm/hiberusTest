package com.hiberius.paymentinitiation.adapter.in.web;

import com.hiberius.paymentinitiation.domain.model.ExternalReference;
import com.hiberius.paymentinitiation.domain.model.Iban;
import com.hiberius.paymentinitiation.domain.model.Money;
import com.hiberius.paymentinitiation.domain.model.PaymentOrder;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderInitiation;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderStatus;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderStatusSnapshot;
import com.hiberius.paymentinitiation.domain.model.RemittanceInfo;
import com.hiberius.paymentinitiation.generated.model.AccountReference;
import com.hiberius.paymentinitiation.generated.model.InstructedAmount;
import com.hiberius.paymentinitiation.generated.model.PaymentOrderInitiationRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentOrderApiMapperTest {

    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2025-10-15T12:00:00Z"), ZoneOffset.UTC);

    private final PaymentOrderApiMapper mapper = new PaymentOrderApiMapper();

    @Test
    void mapsRequestToDomain() {
        var req = new PaymentOrderInitiationRequest(
                "EXT-1",
                new AccountReference("EC12DEBTOR"),
                new AccountReference("EC98CREDITOR"),
                new InstructedAmount(100.0, "USD"),
                LocalDate.of(2025, 12, 1));
        req.setRemittanceInformation("memo");

        var domain = mapper.toDomain(req);

        assertThat(domain.externalReference().value()).isEqualTo("EXT-1");
        assertThat(domain.money().amount()).isEqualByComparingTo(BigDecimal.valueOf(100.0));
        assertThat(domain.remittance()).isPresent();
    }

    @Test
    void mapsStatusSnapshotToApi() {
        var snap = new PaymentOrderStatusSnapshot(
                PaymentOrderId.of("PO-1"),
                PaymentOrderStatus.RECEIVED,
                Instant.parse("2025-10-15T12:00:00Z"));

        var view = mapper.toStatusView(snap);

        assertThat(view.getPaymentOrderId()).isEqualTo("PO-1");
        assertThat(view.getLastUpdated()).isEqualTo(OffsetDateTime.ofInstant(snap.lastUpdated(), ZoneOffset.UTC));
    }

    @Test
    void mapsOrderToCreationResponse() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-CR"),
                Iban.of("EC12DEBTOR"),
                Iban.of("EC98CREDITOR"),
                Money.of(new BigDecimal("50.00"), "EUR"),
                Optional.empty(),
                LocalDate.of(2025, 11, 1));
        PaymentOrder order = PaymentOrder.initiate(PaymentOrderId.of("PO-CREATION"), initiation, CLOCK);

        var response = mapper.toCreationResponse(order);

        assertThat(response.getPaymentOrderId()).isEqualTo("PO-CREATION");
        assertThat(response.getStatus()).isEqualTo(
                com.hiberius.paymentinitiation.generated.model.PaymentOrderStatus.RECEIVED);
        assertThat(response.getCreatedAt()).isEqualTo(OffsetDateTime.ofInstant(order.createdAt(), ZoneOffset.UTC));
        assertThat(response.getExternalReference()).isEqualTo("EXT-CR");
    }

    @Test
    void mapsOrderToApiWithRemittance() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-API"),
                Iban.of("EC12DEBTOR"),
                Iban.of("EC98CREDITOR"),
                Money.of(new BigDecimal("99.50"), "USD"),
                Optional.of(RemittanceInfo.of("Ref factura")),
                LocalDate.of(2025, 12, 15));
        PaymentOrder order = PaymentOrder.initiate(PaymentOrderId.of("PO-API-1"), initiation, CLOCK);

        var api = mapper.toApi(order);

        assertThat(api.getPaymentOrderId()).isEqualTo("PO-API-1");
        assertThat(api.getExternalReference()).isEqualTo("EXT-API");
        assertThat(api.getDebtorAccount().getIban()).isEqualTo("EC12DEBTOR");
        assertThat(api.getCreditorAccount().getIban()).isEqualTo("EC98CREDITOR");
        assertThat(api.getInstructedAmount().getAmount()).isEqualTo(99.5);
        assertThat(api.getInstructedAmount().getCurrency()).isEqualTo("USD");
        assertThat(api.getRequestedExecutionDate()).isEqualTo(LocalDate.of(2025, 12, 15));
        assertThat(api.getStatus()).isEqualTo(
                com.hiberius.paymentinitiation.generated.model.PaymentOrderStatus.RECEIVED);
        assertThat(api.getCreatedAt()).isEqualTo(OffsetDateTime.ofInstant(order.createdAt(), ZoneOffset.UTC));
        assertThat(api.getStatusChangedAt()).isEqualTo(OffsetDateTime.ofInstant(order.statusChangedAt(), ZoneOffset.UTC));
        assertThat(api.getRemittanceInformation()).isEqualTo("Ref factura");
    }

    @Test
    void mapsOrderToApiWithoutRemittance() {
        var initiation = new PaymentOrderInitiation(
                ExternalReference.of("EXT-NO-REM"),
                Iban.of("A"),
                Iban.of("B"),
                Money.of(BigDecimal.ONE, "EUR"),
                Optional.empty(),
                LocalDate.of(2025, 10, 20));
        PaymentOrder order = PaymentOrder.initiate(PaymentOrderId.of("PO-NOREM"), initiation, CLOCK);

        var api = mapper.toApi(order);

        assertThat(api.getRemittanceInformation()).isNull();
    }

    @Test
    void toDomainRejectsNullDebtorAccount() {
        var req = new PaymentOrderInitiationRequest(
                "EXT",
                null,
                new AccountReference("EC98CREDITOR"),
                new InstructedAmount(1.0, "USD"),
                LocalDate.of(2025, 12, 1));

        assertThatThrownBy(() -> mapper.toDomain(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("debtorAccount");
    }

    @Test
    void toDomainRejectsNullInstructedAmount() {
        var req = new PaymentOrderInitiationRequest(
                "EXT",
                new AccountReference("EC12DEBTOR"),
                new AccountReference("EC98CREDITOR"),
                null,
                LocalDate.of(2025, 12, 1));

        assertThatThrownBy(() -> mapper.toDomain(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("instructedAmount");
    }
}
