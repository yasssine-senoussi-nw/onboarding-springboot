package com.nimbleways.springboilerplate.features.authentication.domain.exceptions;

import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.RefreshToken;

public class RefreshTokenExpiredOrNotFoundException extends AbstractAuthenticationDomainException {
    public RefreshTokenExpiredOrNotFoundException(RefreshToken refreshToken) {
        super(String.format("RefreshToken has expired or not present in database: %s", refreshToken.value()));
    }
}
