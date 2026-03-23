package com.hiberius.paymentinitiation.port.out;

import com.hiberius.paymentinitiation.domain.model.PaymentOrder;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;

import java.util.Optional;

public interface PaymentOrderRepository {

    void save(PaymentOrder order);

    Optional<PaymentOrder> findById(PaymentOrderId id);
}
