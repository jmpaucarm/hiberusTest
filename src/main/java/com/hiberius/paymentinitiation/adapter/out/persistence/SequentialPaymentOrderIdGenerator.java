package com.hiberius.paymentinitiation.adapter.out.persistence;

import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.port.out.PaymentOrderIdGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class SequentialPaymentOrderIdGenerator implements PaymentOrderIdGenerator {

    private final AtomicLong sequence = new AtomicLong();

    @Override
    public PaymentOrderId next() {
        return PaymentOrderId.of("PO-" + String.format("%06d", sequence.incrementAndGet()));
    }
}
