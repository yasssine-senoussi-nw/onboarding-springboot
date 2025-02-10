package com.nimbleways.springboilerplate.features.authentication.domain.exceptions;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;

public class UnknownEmailException extends AbstractAuthenticationDomainException {
    public UnknownEmailException(final Email email) {
        super(String.format("Email not found: %s", email.value()));
    }
}
