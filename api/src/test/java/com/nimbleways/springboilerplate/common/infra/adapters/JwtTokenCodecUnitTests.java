package com.nimbleways.springboilerplate.common.infra.adapters;

import com.nimbleways.springboilerplate.common.infra.properties.JwtProperties;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.TokenClaimsCodecPortContractTests;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.AccessToken;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeRandomGenerator;
import java.time.Instant;
import java.time.temporal.ChronoField;

@UnitTest
public class JwtTokenCodecUnitTests extends TokenClaimsCodecPortContractTests {

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
    protected AccessToken getTokenWithoutRoles() {
        return new AccessToken(
            "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjZmNkMjA4NDk1ZDUzNWVmYTZlN2RmZjlmOTg3NjRkYSIsInN1YiI6IjIzYTBiYTcyLWE3NzUtNGVmZS1iN2EwLTgyMTViMDViYzIyOSx1c2VybmFtZSIsImlzcyI6Im15YXBwIiwiaWF0IjoxNzA5MjI2OTcwLCJleHAiOjE3MDkyMjY5NzF9.kv9KtJkciWIhiexl6Ylx3gnaezOHVDY6ixIaVROVqDg");
    }

    @Override
    protected AccessToken getTokenWithInvalidRolesArrayClaim() {
        return new AccessToken(
            "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjZmNkMjA4NDk1ZDUzNWVmYTZlN2RmZjlmOTg3NjRkYSIsInN1YiI6IjFmMTU3ZGMyLWMzZDEtNDFkNC04OGQwLTk3M2NjNWExNThkMCx1c2VybmFtZSIsImlzcyI6Im15YXBwIiwiaWF0IjoxNzA5MjI3MTQxLCJleHAiOjE3MDkyMjcxNDIsInNjb3BlIjpbMSwyLDNdfQ.3AY7vsJzUkzh5fNoP0_y9oUEDp6z1yEHvFnCvwLzyao");
    }

    @Override
    protected AccessToken getTokenWithInvalidRolesScalarClaim() {
        return new AccessToken(
            "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjZmNkMjA4NDk1ZDUzNWVmYTZlN2RmZjlmOTg3NjRkYSIsInN1YiI6ImJkNTAzNzc4LTgxMjMtNDZiMy05MjA4LTE4ZGI0YzdhYWNmOCx1c2VybmFtZSIsImlzcyI6Im15YXBwIiwiaWF0IjoxNzA5MjI3MTk5LCJleHAiOjE3MDkyMjcyMDAsInNjb3BlIjoxMH0.duOIIRK8XcvMMwcmJC6-Nvads-aOQ8zVi_Y_rVzImvA");
    }
}
