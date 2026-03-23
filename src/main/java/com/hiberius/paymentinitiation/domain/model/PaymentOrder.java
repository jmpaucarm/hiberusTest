package com.hiberius.paymentinitiation.domain.model;

import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

public final class PaymentOrder {

    private final PaymentOrderId id;
    private final ExternalReference externalReference;
    private final Iban debtorIban;
    private final Iban creditorIban;
    private final Money money;
    private final Optional<RemittanceInfo> remittance;
    private final LocalDate requestedExecutionDate;
    private PaymentOrderStatus status;
    private final Instant createdAt;
    private Instant statusChangedAt;

    private PaymentOrder(
            PaymentOrderId id,
            ExternalReference externalReference,
            Iban debtorIban,
            Iban creditorIban,
            Money money,
            Optional<RemittanceInfo> remittance,
            LocalDate requestedExecutionDate,
            PaymentOrderStatus status,
            Instant createdAt,
            Instant statusChangedAt) {
        this.id = id;
        this.externalReference = externalReference;
        this.debtorIban = debtorIban;
        this.creditorIban = creditorIban;
        this.money = money;
        this.remittance = remittance;
        this.requestedExecutionDate = requestedExecutionDate;
        this.status = status;
        this.createdAt = createdAt;
        this.statusChangedAt = statusChangedAt;
    }

    /**
     * Factory for a new payment order: applies business rules and sets initial status {@link PaymentOrderStatus#RECEIVED}.
     */
    public static PaymentOrder initiate(
            PaymentOrderId id,
            PaymentOrderInitiation initiation,
            Clock clock) {
        if (sameAccount(initiation.debtorIban(), initiation.creditorIban())) {
            throw new BusinessRuleViolationException("debtor and creditor accounts must differ");
        }
        LocalDate today = LocalDate.now(clock);
        if (initiation.requestedExecutionDate().isBefore(today)) {
            throw new BusinessRuleViolationException("requestedExecutionDate must not be in the past");
        }
        Instant now = clock.instant();
        return new PaymentOrder(
                id,
                initiation.externalReference(),
                initiation.debtorIban(),
                initiation.creditorIban(),
                initiation.money(),
                initiation.remittance(),
                initiation.requestedExecutionDate(),
                PaymentOrderStatus.RECEIVED,
                now,
                now);
    }

    private static boolean sameAccount(Iban a, Iban b) {
        return a.value().equalsIgnoreCase(b.value());
    }

    public PaymentOrderId id() {
        return id;
    }

    public ExternalReference externalReference() {
        return externalReference;
    }

    public Iban debtorIban() {
        return debtorIban;
    }

    public Iban creditorIban() {
        return creditorIban;
    }

    public Money money() {
        return money;
    }

    public Optional<RemittanceInfo> remittance() {
        return remittance;
    }

    public LocalDate requestedExecutionDate() {
        return requestedExecutionDate;
    }

    public PaymentOrderStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant statusChangedAt() {
        return statusChangedAt;
    }

    public void replaceStatus(PaymentOrderStatus newStatus, Clock clock) {
        this.status = newStatus;
        this.statusChangedAt = clock.instant();
    }
}
