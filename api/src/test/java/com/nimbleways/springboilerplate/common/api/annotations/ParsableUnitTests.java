package com.nimbleways.springboilerplate.common.api.annotations;

import com.nimbleways.springboilerplate.common.utils.ParsableChecker;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import jakarta.validation.ConstraintDefinitionException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
class ParsableUnitTests {
    private final static String VALID_VALUE = "15";
    private final static String INVALID_VALUE = "fifteen";

    private Validator validator;

    @BeforeEach
    public void setUp() {
        try (LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean()) {
            factoryBean.afterPropertiesSet();
            validator = factoryBean.getValidator();
        }
    }

    @ParameterizedTest
    @MethodSource("provideObjectFactories")
    void validator_returns_no_violation_on_valid_objects(Function<String, Object> factory) {
        Object validObject = factory.apply(VALID_VALUE);

        Set<ConstraintViolation<Object>> violations = validator.validate(validObject);

        assertTrue(violations.isEmpty(), "There should be no validation errors for valid input.");
    }


    @ParameterizedTest
    @MethodSource("provideObjectFactories")
    void validator_returns_violations_on_class_with_invalid_field_value(Function<String, Object> factory) {
        Object invalidObject = factory.apply(INVALID_VALUE);

        Set<ConstraintViolation<Object>> violations = validator.validate(invalidObject);

        assertViolationsReportInvalidValueInField1(violations);
    }

    @Test
    void validator_throws_if_checker_does_not_have_default_constructor() {
        InputRecordCheckerWithoutDefaultConstructor request = new InputRecordCheckerWithoutDefaultConstructor(VALID_VALUE);

        Exception exception = assertThrows(Exception.class, () -> validator.validate(request));

        assertEquals(ValidationException.class, exception.getClass());
        assertEquals(ConstraintDefinitionException.class, exception.getCause().getClass());
    }

    private static Stream<Arguments> provideObjectFactories() {
        return Stream.of(
                Arguments.of((Function<String, Object>) (InputClass::new)),
                Arguments.of((Function<String, Object>) (InputRecord::new)),
                Arguments.of((Function<String, Object>) (InputRecordCheckerWithValidInstanceField::new)),
                Arguments.of((Function<String, Object>) (InputRecordCheckerWithInstanceFieldOfBadType::new)),
                Arguments.of((Function<String, Object>) (InputRecordCheckerWithPrivateInstanceField::new)),
                Arguments.of((Function<String, Object>) (InputRecordCheckerWithNonStaticInstanceField::new)),
                Arguments.of((Function<String, Object>) (InputRecordCheckerWithNonFinalInstanceField::new))
        );
    }

    private <T> void assertViolationsReportInvalidValueInField1(Set<ConstraintViolation<T>> violations) {
        assertFalse(violations.isEmpty(), "There should be validation errors for invalid input.");
        ConstraintViolation<T> violation = violations.iterator().next();

        assertEquals(Parsable.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("Invalid value", violation.getMessage());
        assertEquals("field1", violation.getPropertyPath().toString());
        assertEquals(INVALID_VALUE, violation.getInvalidValue());
    }

    @Getter
    @RequiredArgsConstructor
    private final static class InputClass {
        @Parsable(Checker.class)
        private final String field1;
    }

    private record InputRecord (
        @Parsable(Checker.class)
        String field1
    ){}

    final static class Checker implements ParsableChecker {
        @Override
        public boolean canParse(Object value) {
            return VALID_VALUE.equals(value);
        }
    }

    private record InputRecordCheckerWithoutDefaultConstructor (
        @Parsable(Checker.class)
        String field1
    ){
        final static class Checker implements ParsableChecker {
            public Checker(String param) {
                param.notify();
            }
            @Override
            public boolean canParse(Object value) {
                return VALID_VALUE.equals(value);
            }
        }
    }

    private record InputRecordCheckerWithValidInstanceField (
            @Parsable(Checker.class)
            String field1
    ){
        final static class Checker implements ParsableChecker {
            public static final Checker INSTANCE = new Checker();
            @Override
            public boolean canParse(Object value) {
                return VALID_VALUE.equals(value);
            }
        }
    }

    private record InputRecordCheckerWithInstanceFieldOfBadType (
            @Parsable(Checker.class)
            String field1
    ){
        final static class Checker implements ParsableChecker {
            public static final String INSTANCE = "abc";
            @Override
            public boolean canParse(Object value) {
                return VALID_VALUE.equals(value);
            }
        }
    }

    private record InputRecordCheckerWithPrivateInstanceField (
            @Parsable(Checker.class)
            String field1
    ){
        final static class Checker implements ParsableChecker {
            @SuppressWarnings("PMD.UnusedPrivateField")
            private static final Checker INSTANCE = new Checker();
            @Override
            public boolean canParse(Object value) {
                return VALID_VALUE.equals(value);
            }
        }
    }

    private record InputRecordCheckerWithNonStaticInstanceField (
            @Parsable(Checker.class)
            String field1
    ){
        final static class Checker implements ParsableChecker {
            @SuppressWarnings("PMD.FinalFieldCouldBeStatic")
            public final Checker INSTANCE = null;
            @Override
            public boolean canParse(Object value) {
                return VALID_VALUE.equals(value);
            }
        }
    }

    private record InputRecordCheckerWithNonFinalInstanceField (
            @Parsable(Checker.class)
            String field1
    ){
        final static class Checker implements ParsableChecker {
            @SuppressWarnings("PMD.MutableStaticState")
            public static Checker INSTANCE = new Checker();
            @Override
            public boolean canParse(Object value) {
                return VALID_VALUE.equals(value);
            }
        }
    }

    private record InputRecordCheckerWithNullInstanceField (
            @Parsable(Checker.class)
            String field1
    ){
        final static class Checker implements ParsableChecker {
            public static final Checker INSTANCE = null;
            @Override
            public boolean canParse(Object value) {
                return VALID_VALUE.equals(value);
            }
        }
    }
}
