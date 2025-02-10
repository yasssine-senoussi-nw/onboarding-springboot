package com.nimbleways.springboilerplate.common.api.exceptionhandling;

import com.nimbleways.springboilerplate.common.api.exceptionhandling.GlobalExceptionHandlerIntegrationTests.InMemoryEventListener;
import com.nimbleways.springboilerplate.common.api.events.UnhandledExceptionEvent;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Username;
import com.nimbleways.springboilerplate.common.utils.collections.Immutable;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.AccessTokenDecodingException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.BadUserCredentialException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.CannotCreateUserSessionInRepositoryException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.RefreshAndAccessTokensMismatchException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.RefreshTokenExpiredOrNotFoundException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.UnknownUsernameException;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.AccessToken;
import com.nimbleways.springboilerplate.features.users.domain.exceptions.UsernameAlreadyExistsInRepositoryException;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import com.nimbleways.springboilerplate.testhelpers.utils.ClassFinder;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;

import static com.nimbleways.springboilerplate.Application.BASE_PACKAGE_NAME;
import static com.nimbleways.springboilerplate.common.api.exceptionhandling.ExceptionHandlingFakeEndpoint.*;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.SimpleFixture.aRefreshToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings({"PMD.ExcessiveImports"})
@WebMvcTest(ExceptionHandlingFakeEndpoint.class)
@Import({InMemoryEventListener.class})
class GlobalExceptionHandlerIntegrationTests extends BaseWebMvcIntegrationTests {
    @Autowired
    private ExceptionHandlingFakeEndpoint fakeEndpoint;
    @Autowired
    private InMemoryEventListener inMemoryEventListener;

    @BeforeEach
    public void clearLogs() {
        inMemoryEventListener.clear();
    }

    @Test
    void returns_problemDetails_when_using_wrong_http_method() throws Exception {
        // GIVEN
        HttpStatus expectedHttpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        String expectedJsonBody = """
                    {"type":"about:blank","title":"Method Not Allowed","status":405,
                    "detail":"Method 'POST' is not supported.","instance":"/exception-handling/get"}""";

        // WHEN
        mockMvc
            .perform(
                post(PERMIT_ALL_GET_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
            )

        // THEN
            .andExpect(status().is(expectedHttpStatus.value()))
            .andExpect(content().json(expectedJsonBody));
    }

    @Test
    void returns_problemDetails_when_request_body_complex_validation_fails() throws Exception {
        // WHEN
        mockMvc
                .perform(
                        post(POST_WITH_BODY_COMPLEX_VALIDATION)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )

                // THEN
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().json("""
                        {"type":"about:blank","title":"errors.input_failed_validation",
                        "status":400,"detail":"errors.input_failed_validation",
                        "instance":"/exception-handling/complex/post",
                        "errorMetadata":{"errors":[{"constraint":"ComplexValidation",
                        "message":"Complex validation failed"}]}}"""));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRequestBodies")
    void returns_problemDetails_when_request_body_validation_fails(
        String requestBody,
        String expectedResponseBody
    ) throws Exception {
        // WHEN
        mockMvc
            .perform(
                post(POST_WITH_BODY_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )

        // THEN
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
            .andExpect(content().json(expectedResponseBody));
    }

    @ParameterizedTest
    @MethodSource("provideExceptions")
    void returns_problemDetails_when_an_exception_is_thrown(
        Exception exceptionToThrow,
        HttpStatus expectedHttpStatus,
        String expectedJsonBody
    ) throws Exception {
        // GIVEN
        fakeEndpoint.setExceptionToThrow(exceptionToThrow);

        // WHEN
        mockMvc
            .perform(get(EXCEPTION_GET_ENDPOINT))

        // THEN
            .andExpect(status().is(expectedHttpStatus.value()))
            .andExpect(content().json(expectedJsonBody));

        ImmutableList<UnhandledExceptionEvent> events = inMemoryEventListener.getEvents();
        assertEquals(1, events.size());
        assertEquals(exceptionToThrow, events.get(0).exception());
    }

    @Test
    void ensure_all_custom_exceptions_are_listed_in_provideExceptions() {
        // GIVEN
        ImmutableList<? extends Class<?>> definedExceptions = ClassFinder
            .findAllNonAbstractExceptions(BASE_PACKAGE_NAME);
        List<? extends Class<?>> testedExceptions = provideExceptions()
            .map(arg -> arg.get()[0].getClass())
            .toList();

        // WHEN
        MutableList<? extends Class<?>> untestedExceptions = definedExceptions.toList();
        untestedExceptions.removeAll(testedExceptions);

        // THEN
        assertEquals(List.of(), untestedExceptions, "all custom exceptions must be declared in 'provideExceptions()'");
    }

    private static Stream<Arguments> provideInvalidRequestBodies() {
        return Stream.of(
            // Empty JSON object
            Arguments.of(
                "{}", """
                {"type":"about:blank","title":"errors.input_failed_validation","status":400,"detail":"errors.input_failed_validation",
                "instance":"/exception-handling/post","errorMetadata":{"fields":[
                {"field":"notBlankString","constraint":"NotBlank","message":"must not be blank","rejectedValue":null},
                {"field":"requiredInt","constraint":"NotNull","message":"must not be null","rejectedValue":null}]}}"""),

            // Invalid JSON request body
            Arguments.of("invalid", """
                {"type":"about:blank","title":"Bad Request","status":400,
                "detail":"Failed to read request","instance":"/exception-handling/post"}"""),

            // Missing required field
            Arguments.of("""
                    {"requiredInt":10}""", """
                {"type":"about:blank","title":"errors.input_failed_validation","status":400,"detail":"errors.input_failed_validation",
                "instance":"/exception-handling/post","errorMetadata":{"fields":[
                {"field":"notBlankString","constraint":"NotBlank","message":"must not be blank","rejectedValue":null}]}}"""),

            // Wrong data type
            Arguments.of("""
                    {"requiredInt":"random string"}""", """
                {"type":"about:blank","title":"errors.input_bad_format","status":400,
                "detail":"errors.input_bad_format","instance":"/exception-handling/post"}"""),

            // Empty request body
            Arguments.of(
                "", """
                {"type":"about:blank","title":"errors.missing_body","status":400,
                "detail":"errors.missing_body","instance":"/exception-handling/post"}""")
        );
    }

    private static Stream<Arguments> provideExceptions() {
        final String unauthorizedJsonResponse = """
            {"type":"about:blank","title":"errors.unauthorized","status":401,
            "detail":"errors.unauthorized","instance":"/exception-handling/throw"}""";

        final String internalServerErrorJsonResponse = """
            {"type":"about:blank","title":"errors.internal_server_error","status":500,
            "detail":"errors.internal_server_error","instance":"/exception-handling/throw"}""";

        return Stream.of(
            Arguments.of(
                new AccessDeniedException(""),
                HttpStatus.FORBIDDEN, """
                {"type":"about:blank","title":"errors.access_denied","status":403,
                "detail":"errors.access_denied","instance":"/exception-handling/throw"}"""),

            Arguments.of(
                new FakeException("this is an unexpected error"),
                HttpStatus.INTERNAL_SERVER_ERROR, internalServerErrorJsonResponse),

            Arguments.of(
                new AccessTokenDecodingException("", new AccessToken("")),
                HttpStatus.UNAUTHORIZED, unauthorizedJsonResponse),

            Arguments.of(
                new BadUserCredentialException(new Username("")),
                HttpStatus.UNAUTHORIZED, unauthorizedJsonResponse),

            Arguments.of(
                new UsernameAlreadyExistsInRepositoryException(
                    new Username(""), new DataIntegrityViolationException("")),
                HttpStatus.BAD_REQUEST, """
                {"type":"about:blank","title":"errors.username_already_exists","status":400,
                "detail":"errors.username_already_exists","instance":"/exception-handling/throw"}"""),

            Arguments.of(
                new RefreshAndAccessTokensMismatchException(UUID.randomUUID(), UUID.randomUUID()),
                HttpStatus.UNAUTHORIZED, unauthorizedJsonResponse),

            Arguments.of(
                new RefreshTokenExpiredOrNotFoundException(aRefreshToken()),
                HttpStatus.UNAUTHORIZED, unauthorizedJsonResponse),

            Arguments.of(
                new UnknownUsernameException(new Username("")),
                HttpStatus.UNAUTHORIZED, unauthorizedJsonResponse),

            Arguments.of(
                new CannotCreateUserSessionInRepositoryException(
                    new Username(""),
                    new DataIntegrityViolationException("")),
                HttpStatus.INTERNAL_SERVER_ERROR, internalServerErrorJsonResponse)
        );
    }

    static class InMemoryEventListener {
        private final ConcurrentLinkedQueue<UnhandledExceptionEvent> eventQueue = new ConcurrentLinkedQueue<>();

        @EventListener
        public void logEvent(UnhandledExceptionEvent event) {
            eventQueue.add(event);
        }

        public ImmutableList<UnhandledExceptionEvent> getEvents() {
            return Immutable.list.ofAll(eventQueue);
        }

        public void clear() {
            eventQueue.clear();
        }
    }
}

