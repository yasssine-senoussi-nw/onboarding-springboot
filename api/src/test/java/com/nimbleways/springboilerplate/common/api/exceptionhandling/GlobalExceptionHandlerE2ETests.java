package com.nimbleways.springboilerplate.common.api.exceptionhandling;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nimbleways.springboilerplate.testhelpers.BaseE2ETests;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Import({ExceptionHandlingFakeEndpoint.class})
class GlobalExceptionHandlerE2ETests extends BaseE2ETests {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public JwtDecoder decoder() {
            return token -> {
                throw new FakeException();
            };
        }
    }

    @Test
    void returns_problemDetail_when_an_exception_is_thrown_during_authentication() throws JSONException {
        // GIVEN
        RequestEntity<Void> request = RequestEntity
            .get(ExceptionHandlingFakeEndpoint.PERMIT_ALL_GET_ENDPOINT)
            .header(HttpHeaders.COOKIE, "accessToken=x")
            .build();
        String expectedJson = """
            {"type":"about:blank","title":"errors.internal_server_error","status":500,
            "detail":"errors.internal_server_error","instance":"/api/exception-handling/get"}""";

        // WHEN
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        // THEN
        JSONAssert.assertEquals(expectedJson, response.getBody(), JSONCompareMode.STRICT);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
