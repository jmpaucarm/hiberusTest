package com.hiberius.paymentinitiation.domain.exception;

/**
 * Request referred to a resource that does not exist. Maps to HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final String resourceId;
    private final String problemTitle;

    protected ResourceNotFoundException(
            String resourceType, String resourceId, String message, String problemTitle) {
        super(message);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.problemTitle = problemTitle;
    }

    public String resourceType() {
        return resourceType;
    }

    public String resourceId() {
        return resourceId;
    }

    /** Short title for RFC 7807 {@code title} field. */
    public String problemTitle() {
        return problemTitle;
    }
}
