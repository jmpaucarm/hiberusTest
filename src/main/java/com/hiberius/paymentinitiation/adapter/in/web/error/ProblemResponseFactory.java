package com.hiberius.paymentinitiation.adapter.in.web.error;

import com.hiberius.paymentinitiation.generated.model.ProblemDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Builds RFC 7807 {@link ProblemDetails} with consistent {@code type} URIs and {@code code}.
 */
public final class ProblemResponseFactory {

    public static final String TYPE_VALIDATION = "https://api.bank/errors/validation";
    public static final String TYPE_BUSINESS_RULE = "https://api.bank/errors/business-rule";
    public static final String TYPE_NOT_FOUND = "https://api.bank/errors/not-found";
    public static final String TYPE_APPLICATION = "https://api.bank/errors/application";
    public static final String TYPE_INTERNAL = "https://api.bank/errors/internal";

    private static final MediaType PROBLEM_JSON = MediaType.parseMediaType("application/problem+json");

    private ProblemResponseFactory() {}

    public static ProblemDetails problem(
            String type,
            String title,
            int status,
            String detail,
            String instance,
            ApiErrorCode code) {
        ProblemDetails p = new ProblemDetails(title, status);
        p.setType(type);
        p.setDetail(detail);
        p.setInstance(instance);
        p.setCode(code.name());
        return p;
    }

    public static ResponseEntity<ProblemDetails> response(HttpStatus httpStatus, ProblemDetails body) {
        return ResponseEntity.status(httpStatus)
                .contentType(PROBLEM_JSON)
                .body(body);
    }
}
