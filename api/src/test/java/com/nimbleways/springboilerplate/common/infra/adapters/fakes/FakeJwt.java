package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import org.springframework.security.oauth2.jwt.Jwt;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.util.Collections;

public class FakeJwt extends Jwt {
    private final ImmutableMap<String, Object> claims;

    public FakeJwt(ImmutableMap<String, Object> claims) {
        super("token", Instant.now(), Instant.now().plusSeconds(3600),
                Collections.singletonMap("alg", "none"), claims);
        this.claims = claims;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @Nullable T getClaim(String claim) {
        return (T) claims.get(claim);
    }
}
