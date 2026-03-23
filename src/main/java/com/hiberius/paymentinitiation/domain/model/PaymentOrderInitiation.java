package com.hiberius.paymentinitiation.domain.model;

import java.time.LocalDate;
import java.util.Optional;

public record PaymentOrderInitiation(
        ExternalReference externalReference,
        Iban debtorIban,
        Iban creditorIban,
        Money money,
        Optional<RemittanceInfo> remittance,
        LocalDate requestedExecutionDate) {
}
