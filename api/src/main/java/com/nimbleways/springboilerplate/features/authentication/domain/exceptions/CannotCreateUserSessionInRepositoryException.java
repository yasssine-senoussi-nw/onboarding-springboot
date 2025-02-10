package com.nimbleways.springboilerplate.features.authentication.domain.exceptions;

import com.nimbleways.springboilerplate.common.domain.exceptions.AbstractDomainException;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;

public class CannotCreateUserSessionInRepositoryException  extends AbstractDomainException {

    public CannotCreateUserSessionInRepositoryException(Email email, Throwable cause) {
        super(
            String.format("Cannot create UserSession in repository for email '%s'", email.value()),
            cause
        );
    }

}
