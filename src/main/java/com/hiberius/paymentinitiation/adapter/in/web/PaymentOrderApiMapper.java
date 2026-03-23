package com.hiberius.paymentinitiation.adapter.in.web;

import com.hiberius.paymentinitiation.domain.model.ExternalReference;
import com.hiberius.paymentinitiation.domain.model.Iban;
import com.hiberius.paymentinitiation.domain.model.Money;
import com.hiberius.paymentinitiation.domain.model.PaymentOrder;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderInitiation;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderStatusSnapshot;
import com.hiberius.paymentinitiation.domain.model.RemittanceInfo;
import com.hiberius.paymentinitiation.generated.model.AccountReference;
import com.hiberius.paymentinitiation.generated.model.InstructedAmount;
import com.hiberius.paymentinitiation.generated.model.PaymentOrderCreationResponse;
import com.hiberius.paymentinitiation.generated.model.PaymentOrderInitiationRequest;
import com.hiberius.paymentinitiation.generated.model.PaymentOrderStatusView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class PaymentOrderApiMapper {

    public PaymentOrderInitiation toDomain(PaymentOrderInitiationRequest request) {
        if (request.getDebtorAccount() == null || request.getCreditorAccount() == null) {
            throw new IllegalArgumentException("debtorAccount and creditorAccount are required");
        }
        Optional<RemittanceInfo> remittance = Optional.ofNullable(request.getRemittanceInformation())
                .filter(s -> !s.isBlank())
                .map(RemittanceInfo::of);

        return new PaymentOrderInitiation(
                ExternalReference.of(request.getExternalReference()),
                Iban.of(request.getDebtorAccount().getIban()),
                Iban.of(request.getCreditorAccount().getIban()),
                toMoney(request.getInstructedAmount()),
                remittance,
                request.getRequestedExecutionDate());
    }

    /**
     * Maps domain aggregate to OpenAPI {@link PaymentOrderCreationResponse}: required fields per contract are
     * {@code paymentOrderId}, {@code status}, {@code createdAt}; {@code externalReference} is echoed when present.
     */
    public PaymentOrderCreationResponse toCreationResponse(PaymentOrder order) {
        return new PaymentOrderCreationResponse(
                        order.id().value(),
                        toApiStatus(order.status()),
                        toOffsetDateTime(order.createdAt()))
                .externalReference(order.externalReference().value());
    }

    /**
     * Maps domain {@link com.hiberius.paymentinitiation.domain.model.PaymentOrder} to the contract DTO for
     * GET /payment-initiation/payment-orders/{id} (full snapshot: accounts, amount, dates, status, remittance if any).
     */
    public com.hiberius.paymentinitiation.generated.model.PaymentOrder toApi(PaymentOrder order) {
        var api = new com.hiberius.paymentinitiation.generated.model.PaymentOrder(
                order.id().value(),
                order.externalReference().value(),
                account(order.debtorIban()),
                account(order.creditorIban()),
                toInstructedAmount(order.money()),
                order.requestedExecutionDate(),
                toApiStatus(order.status()),
                toOffsetDateTime(order.createdAt()),
                toOffsetDateTime(order.statusChangedAt()));
        order.remittance().map(RemittanceInfo::value).ifPresent(api::setRemittanceInformation);
        return api;
    }

    /**
     * Maps {@link PaymentOrderStatusSnapshot} to OpenAPI {@link PaymentOrderStatusView} — only
     * {@code paymentOrderId}, {@code status}, {@code lastUpdated} per contract (no extra fields).
     */
    public PaymentOrderStatusView toStatusView(PaymentOrderStatusSnapshot snapshot) {
        return new PaymentOrderStatusView(
                snapshot.id().value(),
                toApiStatus(snapshot.status()),
                toOffsetDateTime(snapshot.lastUpdated()));
    }

    private static Money toMoney(InstructedAmount amount) {
        if (amount == null) {
            throw new IllegalArgumentException("instructedAmount is required");
        }
        if (amount.getAmount() == null) {
            throw new IllegalArgumentException("instructedAmount.amount is required");
        }
        if (amount.getCurrency() == null || amount.getCurrency().isBlank()) {
            throw new IllegalArgumentException("instructedAmount.currency is required");
        }
        BigDecimal value = BigDecimal.valueOf(amount.getAmount());
        return Money.of(value, amount.getCurrency().trim().toUpperCase());
    }

    private static InstructedAmount toInstructedAmount(Money money) {
        return new InstructedAmount(money.amount().doubleValue(), money.currencyCode());
    }

    private static AccountReference account(Iban iban) {
        return new AccountReference(iban.value());
    }

    private static com.hiberius.paymentinitiation.generated.model.PaymentOrderStatus toApiStatus(
            com.hiberius.paymentinitiation.domain.model.PaymentOrderStatus status) {
        return com.hiberius.paymentinitiation.generated.model.PaymentOrderStatus.fromValue(status.name());
    }

    private static OffsetDateTime toOffsetDateTime(java.time.Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
