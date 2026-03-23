package com.hiberius.paymentinitiation.application.service;

import com.hiberius.paymentinitiation.domain.model.PaymentOrder;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderInitiation;
import com.hiberius.paymentinitiation.port.in.CreatePaymentOrderUseCase;
import com.hiberius.paymentinitiation.port.out.PaymentOrderIdGenerator;
import com.hiberius.paymentinitiation.port.out.PaymentOrderRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

/**
 * Application service: generates a new id, applies domain rules via {@link PaymentOrder#initiate},
 * persists the aggregate, and returns it for mapping to the HTTP 201 response.
 */
@Service
public class CreatePaymentOrderService implements CreatePaymentOrderUseCase {

    private final PaymentOrderRepository paymentOrderRepository;
    private final PaymentOrderIdGenerator paymentOrderIdGenerator;
    private final Clock clock;

    public CreatePaymentOrderService(
            PaymentOrderRepository paymentOrderRepository,
            PaymentOrderIdGenerator paymentOrderIdGenerator,
            Clock clock) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.paymentOrderIdGenerator = paymentOrderIdGenerator;
        this.clock = clock;
    }

    @Override
    public PaymentOrder create(PaymentOrderInitiation initiation) {
        PaymentOrder order = PaymentOrder.initiate(paymentOrderIdGenerator.next(), initiation, clock);
        paymentOrderRepository.save(order);
        return order;
    }
}
