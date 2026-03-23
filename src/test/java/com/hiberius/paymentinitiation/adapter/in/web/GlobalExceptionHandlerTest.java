package com.hiberius.paymentinitiation.adapter.in.web;

import com.hiberius.paymentinitiation.application.exception.ApplicationException;
import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;
import com.hiberius.paymentinitiation.domain.exception.PaymentOrderNotFoundException;
import com.hiberius.paymentinitiation.domain.model.PaymentOrderId;
import com.hiberius.paymentinitiation.generated.model.ProblemDetails;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/payment-initiation/payment-orders");
        request.setMethod("POST");
    }

    @Test
    void handleBusinessRuleViolation() {
        ResponseEntity<ProblemDetails> r =
                handler.handleBusinessRule(new BusinessRuleViolationException("accounts must differ"), request);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getStatus()).isEqualTo(422);
        assertThat(r.getBody().getCode()).isEqualTo("BUSINESS_RULE_VIOLATION");
        assertThat(r.getBody().getType()).isEqualTo("https://api.bank/errors/business-rule");
    }

    @Test
    void handleResourceNotFound() {
        ResponseEntity<ProblemDetails> r =
                handler.handleResourceNotFound(new PaymentOrderNotFoundException(PaymentOrderId.of("PO-X")), request);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getStatus()).isEqualTo(404);
        assertThat(r.getBody().getCode()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(r.getBody().getTitle()).isEqualTo("Payment order not found");
    }

    @Test
    void handleApplicationException() {
        ResponseEntity<ProblemDetails> r =
                handler.handleApplication(new ApplicationException("orchestration failed"), request);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getCode()).isEqualTo("APPLICATION_ERROR");
        assertThat(r.getBody().getType()).isEqualTo("https://api.bank/errors/application");
    }

    @Test
    void handleApplicationExceptionCustomCode() {
        ResponseEntity<ProblemDetails> r =
                handler.handleApplication(
                        new ApplicationException("conflict", 409, "DUPLICATE_EXTERNAL_REF"), request);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(r.getBody().getCode()).isEqualTo("DUPLICATE_EXTERNAL_REF");
    }

    @Test
    void handleMethodArgumentNotValid() throws Exception {
        BeanPropertyBindingResult br = new BeanPropertyBindingResult(new Object(), "object");
        br.addError(new FieldError("object", "currency", "must be uppercase"));
        Method m = Dummy.class.getDeclaredMethod("post", String.class);
        MethodParameter mp = new MethodParameter(m, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mp, br);

        ResponseEntity<ProblemDetails> r = handler.handleMethodArgumentNotValid(ex, request);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody().getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(r.getBody().getDetail()).contains("currency");
    }

    @Test
    void handleConstraintViolation() {
        ConstraintViolationException ex = new ConstraintViolationException(Collections.emptySet());

        ResponseEntity<ProblemDetails> r = handler.handleConstraintViolation(ex, request);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody().getCode()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void handleHttpMessageNotReadable() {
        ResponseEntity<ProblemDetails> r =
                handler.handleNotReadable(new HttpMessageNotReadableException("parse error"), request);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody().getTitle()).isEqualTo("Invalid request body");
    }

    @Test
    void handleIllegalArgument() {
        ResponseEntity<ProblemDetails> r =
                handler.handleIllegalArgument(new IllegalArgumentException("bad id"), request);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(r.getBody().getDetail()).isEqualTo("bad id");
    }

    @Test
    void handleUnexpected() {
        ResponseEntity<ProblemDetails> r =
                handler.handleUnexpected(new RuntimeException("boom"), request);

        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(r.getBody().getCode()).isEqualTo("INTERNAL_ERROR");
        assertThat(r.getBody().getDetail()).doesNotContain("boom");
    }

    @SuppressWarnings("unused")
    private static final class Dummy {
        void post(String body) {
            // signature holder for MethodParameter
        }
    }
}
