package com.nimbleways.springboilerplate.common.api.exceptionhandling;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nimbleways.springboilerplate.common.api.events.UnhandledExceptionEvent;
import com.nimbleways.springboilerplate.common.domain.ports.EventPublisherPort;
import java.net.URI;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.checkerframework.checker.nullness.util.NullnessUtil.castNonNull;

abstract class BaseResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final ErrorAttributes ERROR_ATTRIBUTES_PROVIDER = new DefaultErrorAttributes();
    private final EventPublisherPort eventPublisher;

    protected BaseResponseEntityExceptionHandler(EventPublisherPort eventPublisher) {
        super();
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        @NotNull HttpHeaders headers,
        @NotNull HttpStatusCode status,
        @NotNull WebRequest request
    ) {
        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setTitle(ApiErrorCodes.INPUT_VALIDATION_ERROR.code());
        problemDetail.setDetail(ApiErrorCodes.INPUT_VALIDATION_ERROR.code());
        problemDetail.setProperty("errorMetadata", getViolationsMap(ex.getBindingResult()));
        return this.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        @NotNull HttpHeaders headers,
        @NotNull HttpStatusCode status,
        @NotNull WebRequest request
    ) {
        if (ex.getCause() instanceof InvalidFormatException) {
            ProblemDetail body = this.createProblemDetail(ex, status, ApiErrorCodes.INPUT_FORMAT_ERROR.code(),
                null, null, request);
            body.setTitle(ApiErrorCodes.INPUT_FORMAT_ERROR.code());
            return this.handleExceptionInternal(ex, body, headers, status, request);
        }
        if (Objects.toString(ex).contains("Required request body is missing")) {
            ProblemDetail body = this.createProblemDetail(ex, status, ApiErrorCodes.MISSING_BODY_ERROR.code(),
                null, null, request);
            body.setTitle(ApiErrorCodes.MISSING_BODY_ERROR.code());
            return this.handleExceptionInternal(ex, body, headers, status, request);
        }
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleExceptionInternal(
        @NotNull Exception ex,
        @Nullable Object body,
        @NotNull HttpHeaders headers,
        @NotNull HttpStatusCode statusCode,
        @NotNull WebRequest request
    ) {
        eventPublisher.publishEvent(new UnhandledExceptionEvent(ex));
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @Override
    protected @NotNull ResponseEntity<Object> createResponseEntity(
        @Nullable Object body,
        @NotNull HttpHeaders headers,
        @NotNull HttpStatusCode statusCode,
        @NotNull WebRequest request
    ) {
        setOriginalInstance(request, (ProblemDetail) castNonNull(body)); // body is never null here
        return new ResponseEntity<>(body, headers, statusCode);
    }

    @Nullable
    protected ResponseEntity<Object> getDefaultResponseEntity(Exception ex,
        WebRequest request, HttpStatusCode status, ApiErrorCodes errorCode) {
        ProblemDetail body = this.createProblemDetail(ex, status, errorCode.code(),
            null, null, request);
        body.setTitle(errorCode.code());
        return this.handleExceptionInternal(ex, body, new HttpHeaders(), status, request);
    }

    private static void setOriginalInstance(@NotNull WebRequest request, ProblemDetail problemDetail) {
        Map<String, Object> errorAttributes = ERROR_ATTRIBUTES_PROVIDER.getErrorAttributes(
            request,
            ErrorAttributeOptions.defaults()
        );
        Object path = errorAttributes.get("path");
        if (path != null) {
            problemDetail.setInstance(URI.create(path.toString()));
        }
    }

    private Map<String, Object> getViolationsMap(BindingResult bindingResult) {
        List<Map<String, Object>> fieldErrors = new ArrayList<>();
        List<Map<String, Object>> objectErrors = new ArrayList<>();
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                HashMap<String, Object> errorMap = new HashMap<>();
                errorMap.put("field", fieldError.getField());
                errorMap.put("constraint", fieldError.getCode());
                errorMap.put("rejectedValue", fieldError.getRejectedValue());
                errorMap.put("message", fieldError.getDefaultMessage());
                fieldErrors.add(errorMap);
            } else {
                HashMap<String, Object> errorMap = new HashMap<>();
                errorMap.put("constraint", error.getCode());
                errorMap.put("message", error.getDefaultMessage());
                objectErrors.add(errorMap);
            }
        }
        Map<String, Object> metadata = new HashMap<>();
        if (!objectErrors.isEmpty()) {
            metadata.put("errors", objectErrors);
        }
        if (!fieldErrors.isEmpty()) {
            metadata.put("fields", fieldErrors);
        }
        return metadata;
    }
}
