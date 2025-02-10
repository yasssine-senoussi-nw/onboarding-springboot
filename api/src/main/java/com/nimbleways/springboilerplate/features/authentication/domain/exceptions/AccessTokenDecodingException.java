package com.nimbleways.springboilerplate.features.authentication.domain.exceptions;

import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.AccessToken;

public class AccessTokenDecodingException extends AbstractAuthenticationDomainException {

    public AccessTokenDecodingException(String message, AccessToken token) {
        super(String.format("Cannot decode token '%s': %s", token.value(), message));
    }

    public AccessTokenDecodingException(Throwable cause, AccessToken token) {
        super(String.format("Cannot decode token '%s'", token.value()), cause);
    }
}
