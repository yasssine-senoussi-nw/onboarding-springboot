package com.nimbleways.springboilerplate.features.authentication.domain.exceptions;

import com.nimbleways.springboilerplate.common.domain.exceptions.AbstractDomainException;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Username;

public class CannotCreateUserSessionInRepositoryException  extends AbstractDomainException {

    public CannotCreateUserSessionInRepositoryException(Username username, Throwable cause) {
        super(
            String.format("Cannot create UserSession in repository for user '%s'", username.value()),
            cause
        );
    }

}
