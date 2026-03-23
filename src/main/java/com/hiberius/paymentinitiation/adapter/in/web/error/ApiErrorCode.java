package com.hiberius.paymentinitiation.adapter.in.web.error;

/**
 * Machine-readable codes included in {@code ProblemDetails.code} (RFC 7807 extension).
 */
public enum ApiErrorCode {
    VALIDATION_ERROR,
    RESOURCE_NOT_FOUND,
    BUSINESS_RULE_VIOLATION,
    APPLICATION_ERROR,
    INTERNAL_ERROR
}
