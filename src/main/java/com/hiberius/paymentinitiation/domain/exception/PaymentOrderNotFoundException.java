package com.hiberius.paymentinitiation.domain.exception;

import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;

public class PaymentOrderNotFoundException extends ResourceNotFoundException {

    public PaymentOrderNotFoundException(PaymentOrderId id) {
        super(
                "PaymentOrder",
                id.value(),
                "Payment order not found: " + id.value(),
                "Payment order not found");
    }
}
