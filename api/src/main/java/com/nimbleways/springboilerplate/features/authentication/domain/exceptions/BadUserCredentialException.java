package com.nimbleways.springboilerplate.features.authentication.domain.exceptions;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;

public class BadUserCredentialException extends AbstractAuthenticationDomainException {
    public BadUserCredentialException(final Email email) {
        super(String.format("Bad password provided for email: %s", email.value()));
    }
}
