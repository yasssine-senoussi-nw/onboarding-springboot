package com.nimbleways.springboilerplate.features.authentication.domain.exceptions;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Username;

public class UnknownUsernameException extends AbstractAuthenticationDomainException {
    public UnknownUsernameException(final Username username) {
        super(String.format("Username not found: %s", username.value()));
    }
}
