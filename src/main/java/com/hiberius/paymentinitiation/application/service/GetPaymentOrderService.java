package com.hiberius.paymentinitiation.application.service;

import com.hiberius.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.hiberius.paymentinitiation.domain.model.PaymentOrder;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.port.in.GetPaymentOrderUseCase;
import com.hiberius.paymentinitiation.port.out.PaymentOrderRepository;
import org.springframework.stereotype.Service;

/**
 * Retrieves the payment order aggregate from the outbound {@link PaymentOrderRepository} port.
 */
@Service
public class GetPaymentOrderService implements GetPaymentOrderUseCase {

    private final PaymentOrderRepository paymentOrderRepository;

    public GetPaymentOrderService(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @Override
    public PaymentOrder get(PaymentOrderId id) {
        return paymentOrderRepository.findById(id).orElseThrow(() -> new PaymentOrderNotFoundException(id));
    }
}
