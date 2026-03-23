package com.hiberius.paymentinitiation.application.service;

import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderStatusSnapshot;
import com.hiberius.paymentinitiation.port.in.GetPaymentOrderStatusUseCase;
import com.hiberius.paymentinitiation.port.in.GetPaymentOrderUseCase;
import org.springframework.stereotype.Service;

/**
 * Delegates loading to {@link GetPaymentOrderUseCase} so "order exists?" and not-found behaviour are defined once;
 * maps the aggregate to {@link PaymentOrderStatusSnapshot} for the status endpoint only.
 */
@Service
public class GetPaymentOrderStatusService implements GetPaymentOrderStatusUseCase {

    private final GetPaymentOrderUseCase getPaymentOrderUseCase;

    public GetPaymentOrderStatusService(GetPaymentOrderUseCase getPaymentOrderUseCase) {
        this.getPaymentOrderUseCase = getPaymentOrderUseCase;
    }

    @Override
    public PaymentOrderStatusSnapshot load(PaymentOrderId id) {
        return PaymentOrderStatusSnapshot.from(getPaymentOrderUseCase.get(id));
    }
}
