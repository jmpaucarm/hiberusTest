package com.hiberius.paymentinitiation.adapter.out.persistence;

import com.hiberius.paymentinitiation.domain.model.PaymentOrder;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.port.out.PaymentOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPaymentOrderRepository implements PaymentOrderRepository {

    private final Map<String, PaymentOrder> store = new ConcurrentHashMap<>();

    @Override
    public void save(PaymentOrder order) {
        store.put(order.id().value(), order);
    }

    @Override
    public Optional<PaymentOrder> findById(PaymentOrderId id) {
        return Optional.ofNullable(store.get(id.value()));
    }
}
