package com.hiberius.paymentinitiation.application.exception;

/**
 * Application-layer error (orchestration, ports, policies not expressible as pure domain rules).
 * Default HTTP status is 422; callers may override for conflicts (409) or other 4xx as needed.
 */
public class ApplicationException extends RuntimeException {

    private final int httpStatus;
    private final String errorCode;

    public ApplicationException(String message) {
        this(message, 422, "APPLICATION_ERROR");
    }

    public ApplicationException(String message, Throwable cause) {
        this(message, 422, "APPLICATION_ERROR", cause);
    }

    public ApplicationException(String message, int httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, int httpStatus, String errorCode, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public int httpStatus() {
        return httpStatus;
    }

    public String errorCode() {
        return errorCode;
    }
}
