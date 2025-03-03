package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable;

public class FakeAuthentication implements Authentication {
    private final Object principal;

    public FakeAuthentication(@Nullable Object principal) {
        this.principal = principal;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    // Other methods can return dummy values (not used in our tests)
    @Override
    public ImmutableList<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
    }

    @Override
    public String getName() {
        return null;
    }
}
