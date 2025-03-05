package com.nimbleways.springboilerplate.common.domain.exceptions;

public abstract class AbstractItemNotFoundException extends AbstractDomainException {

    public AbstractItemNotFoundException(String message) {
        super(message);
    }
}
