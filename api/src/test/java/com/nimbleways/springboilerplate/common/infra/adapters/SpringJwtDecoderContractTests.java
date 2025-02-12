package com.nimbleways.springboilerplate.common.infra.adapters;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.UserPrincipalFixture.aUserPrincipal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeTokenClaimsCodec;
import com.nimbleways.springboilerplate.common.infra.properties.JwtProperties;
import com.nimbleways.springboilerplate.common.utils.collections.Immutable;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.TokenClaims;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserPrincipal;
import com.nimbleways.springboilerplate.testhelpers.annotations.IntegrationTest;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeRandomGenerator;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;

public abstract class SpringJwtDecoderContractTests {
    private TimeProviderPort timeProvider;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    public void createSut() {
        timeProvider = getTimeProvider();
        jwtDecoder = getInstance();
    }

    @Test
    void returns_valid_Jwt() {
        TokenClaims claims = getTokenClaims(timeProvider.instant());
        String token = encode(claims);

        Jwt jwt = jwtDecoder.decode(token);

        assertEquals(claims.expirationTime(), jwt.getExpiresAt());
        assertEquals(claims.creationTime(), jwt.getIssuedAt());
        assertEquals(List.of("USER"), jwt.getClaim("scope"));
        assertEquals(getExpectedSubject(claims.userPrincipal()), jwt.getSubject());
    }

    @Test
    void throws_JwtValidationException_if_expired() {
        TokenClaims claims = getTokenClaims(timeProvider.instant().minusSeconds(1));
        String token = encode(claims);

        JwtValidationException exception = assertThrows(JwtValidationException.class, () -> jwtDecoder.decode(token));

        assertEquals(List.of(OAuth2Error.class), exception.getErrors().stream().map(OAuth2Error::getClass).toList());
    }

    @Test
    void throws_BadJwtException_if_malformed() {
        String token = getMalformedToken();

        Exception exception = assertThrows(Exception.class, () -> jwtDecoder.decode(token));

        assertEquals(BadJwtException.class, exception.getClass());
    }

    private String getExpectedSubject(UserPrincipal userPrincipal) {
        return String.format("%s,%s", userPrincipal.id(), userPrincipal.email().value());
    }

    @NotNull
    private TokenClaims getTokenClaims(Instant expirationTime) {
        return new TokenClaims(
            aUserPrincipal().withRoles(Immutable.set.of(Role.USER)).build(),
            adjustPrecision(expirationTime.minusSeconds(1)),
            adjustPrecision(expirationTime)
        );
    }

    // JWT only support time up to the seconds
    private Instant adjustPrecision(Instant instant) {
        return instant.with(ChronoField.MILLI_OF_SECOND, 0);
    }

    // --------------------------------- Protected Methods ------------------------------- //
    protected abstract JwtDecoder getInstance();

    protected abstract String encode(TokenClaims claims);

    protected abstract String getMalformedToken();

    @NotNull
    protected TimeProviderPort getTimeProvider() {
        return TimeTestConfiguration.fixedTimeProvider();
    }

    // ------------------------- Concrete Implementation Providers ----------------------- //
    @UnitTest
    public static class FakeJwtDecoderUnitTests extends SpringJwtDecoderContractTests {
        private final FakeTokenClaimsCodec fakeTokenClaimsCodec;

        public FakeJwtDecoderUnitTests() {
            super();
            fakeTokenClaimsCodec = new FakeTokenClaimsCodec(getTimeProvider());
        }

        @Override
        protected JwtDecoder getInstance() {
            return fakeTokenClaimsCodec;
        }

        @Override
        protected String encode(TokenClaims claims) {
            return fakeTokenClaimsCodec.encode(claims).value();
        }

        @Override
        protected String getMalformedToken() {
            return "invalidToken";
        }
    }

    @IntegrationTest
    public static class JwtDecoderIntegrationTests extends SpringJwtDecoderContractTests {
        private final JwtTokenClaimsCodec jwtTokenClaimsCodec;

        public JwtDecoderIntegrationTests() {
            super();
            jwtTokenClaimsCodec = new JwtTokenClaimsCodec(
                new JwtProperties("zdtlD3JK56m6wTTgsNFhqzjqPaaaddingFor256bits=", "myapp"),
                new FakeRandomGenerator(),
                getTimeProvider());
        }

        @Override
        protected JwtDecoder getInstance() {
            return jwtTokenClaimsCodec;
        }

        @Override
        protected String encode(TokenClaims claims) {
            return jwtTokenClaimsCodec.encode(claims).value();
        }

        @Override
        protected String getMalformedToken() {
            return "invalidToken";
        }
    }

}
