package com.hiberius.paymentinitiation.port.in;

import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderStatusSnapshot;

/**
 * Inbound port: lightweight status projection for {@code GET .../payment-orders/{id}/status} (contract: id, status, lastUpdated).
 */
public interface GetPaymentOrderStatusUseCase {

    /**
     * @throws com.hiberius.paymentinitiation.domain.exception.PaymentOrderNotFoundException if the order does not exist
     */
    PaymentOrderStatusSnapshot load(PaymentOrderId id);
}
