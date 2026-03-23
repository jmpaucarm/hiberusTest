package com.hiberius.paymentinitiation.port.in;

import com.hiberius.paymentinitiation.domain.model.PaymentOrder;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderInitiation;

public interface CreatePaymentOrderUseCase {

    PaymentOrder create(PaymentOrderInitiation initiation);
}
