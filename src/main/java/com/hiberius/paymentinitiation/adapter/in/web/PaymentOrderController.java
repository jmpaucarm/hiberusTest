package com.hiberius.paymentinitiation.adapter.in.web;

import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.generated.api.PaymentOrderApi;
import com.hiberius.paymentinitiation.generated.model.PaymentOrderCreationResponse;
import com.hiberius.paymentinitiation.generated.model.PaymentOrderInitiationRequest;
import com.hiberius.paymentinitiation.port.in.CreatePaymentOrderUseCase;
import com.hiberius.paymentinitiation.port.in.GetPaymentOrderStatusUseCase;
import com.hiberius.paymentinitiation.port.in.GetPaymentOrderUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class PaymentOrderController implements PaymentOrderApi {

    private final CreatePaymentOrderUseCase createPaymentOrderUseCase;
    private final GetPaymentOrderUseCase getPaymentOrderUseCase;
    private final GetPaymentOrderStatusUseCase getPaymentOrderStatusUseCase;
    private final PaymentOrderApiMapper mapper;

    public PaymentOrderController(
            CreatePaymentOrderUseCase createPaymentOrderUseCase,
            GetPaymentOrderUseCase getPaymentOrderUseCase,
            GetPaymentOrderStatusUseCase getPaymentOrderStatusUseCase,
            PaymentOrderApiMapper mapper) {
        this.createPaymentOrderUseCase = createPaymentOrderUseCase;
        this.getPaymentOrderUseCase = getPaymentOrderUseCase;
        this.getPaymentOrderStatusUseCase = getPaymentOrderStatusUseCase;
        this.mapper = mapper;
    }

    /**
     * POST /payment-initiation/payment-orders — contract: 201 + body {@link PaymentOrderCreationResponse} + {@code Location}.
     */
    @Override
    public ResponseEntity<PaymentOrderCreationResponse> createPaymentOrder(
            PaymentOrderInitiationRequest paymentOrderInitiationRequest) {
        var order = createPaymentOrderUseCase.create(mapper.toDomain(paymentOrderInitiationRequest));
        PaymentOrderCreationResponse body = mapper.toCreationResponse(order);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/payment-initiation/payment-orders/{id}")
                .buildAndExpand(order.id().value())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.LOCATION, location.toASCIIString())
                .body(body);
    }

    /**
     * GET /payment-initiation/payment-orders/{paymentOrderId} — loads domain aggregate via {@link GetPaymentOrderUseCase},
     * maps to OpenAPI {@link com.hiberius.paymentinitiation.generated.model.PaymentOrder}. Not found →
     * {@link com.hiberius.paymentinitiation.domain.exception.PaymentOrderNotFoundException} → 404 (handled globally).
     */
    @Override
    public ResponseEntity<com.hiberius.paymentinitiation.generated.model.PaymentOrder> getPaymentOrderById(
            String paymentOrderId) {
        var order = getPaymentOrderUseCase.get(PaymentOrderId.of(paymentOrderId));
        return ResponseEntity.ok(mapper.toApi(order));
    }

    /**
     * GET /payment-initiation/payment-orders/{paymentOrderId}/status — projection only; not found uses the same
     * exception path as full GET via {@link GetPaymentOrderUseCase} (through {@link GetPaymentOrderStatusUseCase}).
     */
    @Override
    public ResponseEntity<com.hiberius.paymentinitiation.generated.model.PaymentOrderStatusView> getPaymentOrderStatus(
            String paymentOrderId) {
        var snapshot = getPaymentOrderStatusUseCase.load(PaymentOrderId.of(paymentOrderId));
        return ResponseEntity.ok(mapper.toStatusView(snapshot));
    }
}
