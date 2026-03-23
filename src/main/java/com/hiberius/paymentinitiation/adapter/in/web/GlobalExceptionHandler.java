package com.hiberius.paymentinitiation.adapter.in.web;

import com.hiberius.paymentinitiation.adapter.in.web.error.ApiErrorCode;
import com.hiberius.paymentinitiation.adapter.in.web.error.ProblemResponseFactory;
import com.hiberius.paymentinitiation.application.exception.ApplicationException;
import com.hiberius.paymentinitiation.domain.exception.BusinessRuleViolationException;
import com.hiberius.paymentinitiation.domain.exception.ResourceNotFoundException;
import com.hiberius.paymentinitiation.generated.model.ProblemDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static String instance(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ProblemDetails> handleBusinessRule(
            BusinessRuleViolationException ex, HttpServletRequest request) {
        ProblemDetails body =
                ProblemResponseFactory.problem(
                        ProblemResponseFactory.TYPE_BUSINESS_RULE,
                        "Business rule violation",
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        ex.getMessage(),
                        instance(request),
                        ApiErrorCode.BUSINESS_RULE_VIOLATION);
        return ProblemResponseFactory.response(HttpStatus.UNPROCESSABLE_ENTITY, body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetails> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetails body =
                ProblemResponseFactory.problem(
                        ProblemResponseFactory.TYPE_NOT_FOUND,
                        ex.problemTitle(),
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        instance(request),
                        ApiErrorCode.RESOURCE_NOT_FOUND);
        return ProblemResponseFactory.response(HttpStatus.NOT_FOUND, body);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ProblemDetails> handleApplication(
            ApplicationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.httpStatus());
        ProblemDetails body =
                ProblemResponseFactory.problem(
                        ProblemResponseFactory.TYPE_APPLICATION,
                        "Application error",
                        status.value(),
                        ex.getMessage(),
                        instance(request),
                        ApiErrorCode.APPLICATION_ERROR);
        body.setCode(ex.errorCode());
        return ProblemResponseFactory.response(status, body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetails> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detail =
                ex.getBindingResult().getFieldErrors().stream()
                        .findFirst()
                        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                        .orElse("Validation failed");
        ProblemDetails body =
                ProblemResponseFactory.problem(
                        ProblemResponseFactory.TYPE_VALIDATION,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST.value(),
                        detail,
                        instance(request),
                        ApiErrorCode.VALIDATION_ERROR);
        return ProblemResponseFactory.response(HttpStatus.BAD_REQUEST, body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetails> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        String detail =
                ex.getConstraintViolations().stream()
                        .findFirst()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .orElse(ex.getMessage());
        ProblemDetails body =
                ProblemResponseFactory.problem(
                        ProblemResponseFactory.TYPE_VALIDATION,
                        "Validation failed",
                        HttpStatus.BAD_REQUEST.value(),
                        detail,
                        instance(request),
                        ApiErrorCode.VALIDATION_ERROR);
        return ProblemResponseFactory.response(HttpStatus.BAD_REQUEST, body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetails> handleNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        ProblemDetails body =
                ProblemResponseFactory.problem(
                        ProblemResponseFactory.TYPE_VALIDATION,
                        "Invalid request body",
                        HttpStatus.BAD_REQUEST.value(),
                        "Malformed or unreadable JSON",
                        instance(request),
                        ApiErrorCode.VALIDATION_ERROR);
        return ProblemResponseFactory.response(HttpStatus.BAD_REQUEST, body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetails> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDetails body =
                ProblemResponseFactory.problem(
                        ProblemResponseFactory.TYPE_VALIDATION,
                        "Invalid request",
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        instance(request),
                        ApiErrorCode.VALIDATION_ERROR);
        return ProblemResponseFactory.response(HttpStatus.BAD_REQUEST, body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleUnexpected(Exception ex, HttpServletRequest request) {
        LOGGER.error("Unhandled exception for {} {}", request.getMethod(), request.getRequestURI(), ex);
        ProblemDetails body =
                ProblemResponseFactory.problem(
                        ProblemResponseFactory.TYPE_INTERNAL,
                        "Internal server error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred. Please try again later.",
                        instance(request),
                        ApiErrorCode.INTERNAL_ERROR);
        return ProblemResponseFactory.response(HttpStatus.INTERNAL_SERVER_ERROR, body);
    }
}
