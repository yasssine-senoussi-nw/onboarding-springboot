package com.nimbleways.springboilerplate.features.users.domain.exceptions;

import com.nimbleways.springboilerplate.common.domain.exceptions.AbstractDomainException;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;

public class EmailAlreadyExistsInRepositoryException extends AbstractDomainException {

    public EmailAlreadyExistsInRepositoryException(Email email, Throwable cause) {
        super(String.format("Email '%s' already exist in repository", email.value()), cause);
    }
}
