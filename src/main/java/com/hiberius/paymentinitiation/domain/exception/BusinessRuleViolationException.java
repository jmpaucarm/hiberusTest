package com.hiberius.paymentinitiation.domain.exception;

/**
 * Domain invariant or business rule was violated (semantic validation).
 * Maps to HTTP 422 Unprocessable Entity in the API layer.
 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }

    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
