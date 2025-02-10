package com.nimbleways.springboilerplate.common.api.exceptionhandling;

import jakarta.validation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@RestController
class ExceptionHandlingFakeEndpoint {
    public static final String EXCEPTION_GET_ENDPOINT = "/exception-handling/throw";
    public static final String PERMIT_ALL_GET_ENDPOINT = "/exception-handling/get";
    public static final String AUTHENTICATED_GET_ENDPOINT = "/exception-handling/auth_get";
    public static final String POST_WITH_BODY_ENDPOINT = "/exception-handling/post";
    public static final String POST_WITH_BODY_COMPLEX_VALIDATION = "/exception-handling/complex/post";

    private Exception exceptionToThrow;

    @SneakyThrows
    @GetMapping(EXCEPTION_GET_ENDPOINT)
    public void throwException() {
        throw exceptionToThrow;
    }

    @GetMapping(PERMIT_ALL_GET_ENDPOINT)
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> getEndpoint() {
        return ResponseEntity.ok().build();
    }

    @GetMapping(AUTHENTICATED_GET_ENDPOINT)
    @PreAuthorize("isAuthenticated()")
    public void authenticatedGetEndpoint() {
    }

    @PostMapping(POST_WITH_BODY_ENDPOINT)
    public void postEndpoint(@RequestBody @Valid final MyRequest myRequest) {
    }

    @PostMapping(POST_WITH_BODY_COMPLEX_VALIDATION)
    public void postComplexValidationEndpoint(@RequestBody @Valid final MyRequestComplexValidation myRequest) {
    }

    public void setExceptionToThrow(Exception exceptionToThrow) {
        this.exceptionToThrow = exceptionToThrow;
    }

    record MyRequest(
        @NotBlank
        String notBlankString,

        @NotNull
        Integer requiredInt
    ) {}

    @ComplexValidation
    public record MyRequestComplexValidation() {}

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = ComplexValidation.ComplexValidator.class)
    public @interface ComplexValidation {
        String message() default "Complex validation failed";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};

        class ComplexValidator implements ConstraintValidator<ComplexValidation, MyRequestComplexValidation> {
            @Override
            public boolean isValid(MyRequestComplexValidation order, ConstraintValidatorContext constraintContext) {
                return false;
            }
        }
    }
}
