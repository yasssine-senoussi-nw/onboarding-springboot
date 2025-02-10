package com.nimbleways.springboilerplate.features.authentication.domain.exceptions;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Username;

public class BadUserCredentialException extends AbstractAuthenticationDomainException {
    public BadUserCredentialException(final Username username) {
        super(String.format("Bad password provided for username: %s", username.value()));
    }
}
