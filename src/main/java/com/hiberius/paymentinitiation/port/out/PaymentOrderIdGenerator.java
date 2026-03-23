package com.hiberius.paymentinitiation.port.out;

import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;

public interface PaymentOrderIdGenerator {

    PaymentOrderId next();
}
