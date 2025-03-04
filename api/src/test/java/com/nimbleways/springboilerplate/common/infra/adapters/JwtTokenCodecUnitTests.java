package com.nimbleways.springboilerplate.common.infra.adapters;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeAuthentication;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeJwt;
import com.nimbleways.springboilerplate.common.infra.properties.JwtProperties;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.TokenClaimsCodecPortContractTests;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.AccessToken;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeRandomGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UnitTest
class JwtTokenCodecUnitTests extends TokenClaimsCodecPortContractTests {

    private static final JwtTokenClaimsCodec INSTANCE = new JwtTokenClaimsCodec(
        new JwtProperties("zdtlD3JK56m6wTTgsNFhqzjqPaaaddingFor256bits=", "myapp"),
        new FakeRandomGenerator(),
        TimeTestConfiguration.fixedTimeProvider());

    @Override
    protected JwtTokenClaimsCodec getInstance() {
        return INSTANCE;
    }

    @Override
    protected Instant adjustPrecision(Instant instant) {
        return instant.with(ChronoField.MILLI_OF_SECOND, 0);
    }

    @Override
    protected AccessToken getTokenWithoutRole() {
        return new AccessToken(
            "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjZmNkMjA4NDk1ZDUzNWVmYTZlN2RmZjlmOTg3NjRkYSIsInN1YiI6IjIzYTBiYTcyLWE3NzUtNGVmZS1iN2EwLTgyMTViMDViYzIyOSx1c2VybmFtZSIsImlzcyI6Im15YXBwIiwiaWF0IjoxNzA5MjI2OTcwLCJleHAiOjE3MDkyMjY5NzF9.kv9KtJkciWIhiexl6Ylx3gnaezOHVDY6ixIaVROVqDg");
    }

    @Override
    protected AccessToken getTokenWithInvalidRoleArrayClaim() {
        return new AccessToken(
            "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjZmNkMjA4NDk1ZDUzNWVmYTZlN2RmZjlmOTg3NjRkYSIsInN1YiI6IjFmMTU3ZGMyLWMzZDEtNDFkNC04OGQwLTk3M2NjNWExNThkMCx1c2VybmFtZSIsImlzcyI6Im15YXBwIiwiaWF0IjoxNzA5MjI3MTQxLCJleHAiOjE3MDkyMjcxNDIsInNjb3BlIjpbMSwyLDNdfQ.3AY7vsJzUkzh5fNoP0_y9oUEDp6z1yEHvFnCvwLzyao");
    }

    @Override
    protected AccessToken getTokenWithInvalidRoleScalarClaim() {
        return new AccessToken(
            "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjZmNkMjA4NDk1ZDUzNWVmYTZlN2RmZjlmOTg3NjRkYSIsInN1YiI6ImJkNTAzNzc4LTgxMjMtNDZiMy05MjA4LTE4ZGI0YzdhYWNmOCx1c2VybmFtZSIsImlzcyI6Im15YXBwIiwiaWF0IjoxNzA5MjI3MTk5LCJleHAiOjE3MDkyMjcyMDAsInNjb3BlIjoxMH0.duOIIRK8XcvMMwcmJC6-Nvads-aOQ8zVi_Y_rVzImvA");
    }

    @Test
    void getCurrentUserEmail_when_authentication_is_null_returns_empty() {
        // GIVEN
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(null);
        SecurityContextHolder.setContext(context);

        // WHEN
        Optional<Email> result = INSTANCE.getCurrentUserEmail();

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void getCurrentUserEmail_when_principal_is_not_Jwt_returns_empty() {
        // GIVEN
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new FakeAuthentication(null));
        SecurityContextHolder.setContext(context);

        // WHEN
        Optional<Email> result = INSTANCE.getCurrentUserEmail();

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    void getCurrentUserEmail_when_Jwt_has_email_returns_email() {
        // GIVEN
        ImmutableMap<String, Object> claims = ImmutableMap.of("email", "user@example.com");
        Jwt jwt = new FakeJwt(claims);
        Authentication auth = new FakeAuthentication(jwt);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // WHEN
        Optional<Email> result = INSTANCE.getCurrentUserEmail();

        // THEN
        assertTrue(result.isPresent());
        assertEquals("user@example.com", result.get().value());
    }
}
