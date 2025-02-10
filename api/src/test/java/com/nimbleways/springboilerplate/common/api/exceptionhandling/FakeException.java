package com.nimbleways.springboilerplate.common.api.exceptionhandling;

class FakeException extends RuntimeException {

    public FakeException() {
        super();
    }

    public FakeException(String message) {
        super(message);
    }
}
