package com.hiberius.paymentinitiation.port.in;

import com.hiberius.paymentinitiation.domain.model.PaymentOrder;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;

/**
 * Inbound port: load a persisted {@link PaymentOrder} by internal identifier (GET full resource).
 */
public interface GetPaymentOrderUseCase {

    /**
     * @throws com.hiberius.paymentinitiation.domain.exception.PaymentOrderNotFoundException if no order exists for {@code id}
     */
    PaymentOrder get(PaymentOrderId id);
}
